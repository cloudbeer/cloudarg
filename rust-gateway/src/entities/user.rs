use crate::entities::config;
use crate::entities::response_wrapper::{Error, Response};

use hyper::body::Buf;
use hyper::client::HttpConnector;
use hyper::Client;
use hyper_tls::HttpsConnector;

use serde::{Deserialize, Serialize};

type HttpClient = Client<HttpsConnector<HttpConnector>>;
type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct User {
  id: usize,
  open_id: String,
  mobile: Option<String>,
  nick: Option<String>,
  email: Option<String>,
  roles: Vec<String>,
}

impl User {
  pub async fn get(ticket: &str, client: HttpClient) -> Result<Response<User, Error>> {
    let config_ins = config::get();
    let account_url = &config_ins.account_url;
    let user_url = String::from(account_url) + "?ticket=" + ticket;
    let user_url: hyper::Uri = user_url.parse().unwrap();
    let res = client.get(user_url).await?;
    let body = hyper::body::aggregate(res).await?;
    let response = serde_json::from_reader(body.reader())?;
    Ok(response)
  }
}
