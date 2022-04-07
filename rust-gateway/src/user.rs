use crate::config;

use hyper::client::HttpConnector;
use hyper::{Body, Client, Method, Request};
use hyper_tls::HttpsConnector;
// use tokio::io::{self, AsyncWriteExt as _};

type HttpClient = Client<HttpsConnector<HttpConnector>>;
// type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

#[derive(Debug)]
pub struct User {
  id: usize,
  open_id: String,
  mobile: Option<String>,
  nick: Option<String>,
  email: Option<String>,
  roles: Vec<String>,
}

impl User {
  pub async fn get(ticket: &str, client: HttpClient) -> Option<User> {
    println!("---------");
    let config_ins = config::get();
    let account_url = &config_ins.account_url;
    // println!("mysql url: {}", config_ins.mysql.host);
    let user_url = String::from(account_url) + "?ticket=" + ticket;
    println!("account url: {user_url}");

    let req_new = Request::builder()
      .method(Method::GET)
      .uri(user_url)
      .body(Body::empty())
      .unwrap();

    // let res = client.request(req_new).await;
    // match res {
    //   Ok(resp) => {
    //     println!("response is {:?}", resp.body().unwrap());
    //   }
    //   _ => {}
    // };
    None
    // println!("response is {:?}", res_txt);
    // Ok(None)
  }
}

// private Long id;
// @SerializedName("open_id")
// private String openId;
// private String mobile;
// private String nick;
// private String email;
// private String[] roles;
