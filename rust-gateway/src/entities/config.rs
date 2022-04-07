use once_cell::sync::OnceCell;
use regex::Regex;

use std::env;
use std::fs;

use yaml_rust::{Yaml, YamlLoader};

// TODO: 这里的非必填字段建议修改成 Option

#[derive(Debug)]
pub struct MySql {
  pub host: String,
  pub port: u16,
  pub database: String,
  pub username: String,
  pub password: String,
}

#[derive(Debug)]
pub struct Config {
  pub name: String,
  pub description: String,
  pub port: u16,
  pub mysql: MySql,
  pub account_url: String,
  pub ticket_secret: String,
  pub timeout: u16,
  pub redis_uri: String,
}

static CONFIG: OnceCell<Config> = OnceCell::new();

fn as_str<'a>(doc: &'a Yaml, name: &'a str) -> String {
  let xvalue = &doc[name];
  let mut str_value = match xvalue {
    Yaml::Integer(value) => value.to_string(),
    Yaml::String(value) => value.to_string(),
    _ => String::from(""),
  };
  let re = Regex::new(r"\$\{(.*)\}").unwrap();

  str_value = if re.is_match(&str_value[..]) {
    let caps = re.captures(&str_value[..]).unwrap();
    let env_str = &caps[1];
    let c_index = env_str.find(':');
    match c_index {
      None => {
        let error = format!("ENV [{}]", env_str);
        env::var(env_str).expect(&error[..])
      }
      Some(v) => {
        let env_key = &env_str[0..v];
        let env_default = &env_str[(v + 1)..];
        let env_value = env::var(env_key);

        match env_value {
          Err(_) => env_default.to_string(),
          Ok(v) => v,
        }
      }
    }
  } else {
    str_value
  };
  str_value
}

fn as_u16<'a>(doc: &'a Yaml, name: &'a str) -> u16 {
  let ori_val = as_str(doc, name);
  let error = format!("Load config: {} of {} transfer to u16 error", name, ori_val);
  let res: u16 = ori_val.parse().expect(&error[..]);
  res
}

pub fn init() {
  let str_config = fs::read_to_string("./resources/config.yaml")
    .expect("wrong config file [./resources/config.yaml]");
  let docs = YamlLoader::load_from_str(&str_config).unwrap();
  let docs = &docs[0];
  let mysql_docs = &docs["mysql"];
  let mysql = MySql {
    host: as_str(mysql_docs, "host"),
    port: as_u16(mysql_docs, "port"),
    database: as_str(mysql_docs, "database"),
    username: as_str(mysql_docs, "username"),
    password: as_str(mysql_docs, "password"),
  };
  let config = Config {
    name: as_str(docs, "name"),
    description: as_str(docs, "description"),
    port: as_u16(docs, "port"),
    mysql: mysql,
    account_url: as_str(docs, "account_url"),
    ticket_secret: as_str(docs, "ticket_secret"),
    timeout: as_u16(docs, "timeout"),
    redis_uri: as_str(docs, "redis_uri"),
  };
  CONFIG.set(config).expect("error config data.");
}

pub fn get() -> &'static Config {
  return CONFIG.get().expect("config not initialized.");
}
