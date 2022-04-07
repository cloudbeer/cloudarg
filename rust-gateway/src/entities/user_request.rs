use hyper::{Body, Request};
use regex::Regex;
use uuid::Uuid;

#[derive(Debug)]
pub struct UserRequest {
  // req: &'a Request<Body>,
  pub ticket: Option<String>,
  pub request_id: String,
}

impl UserRequest {
  pub fn new(req: &Request<Body>) -> UserRequest {
    let query = req.uri().query();
    let re_ticket = Regex::new(r"&?__ticket=(?P<ticket>[^&]+).*").unwrap();
    // let mut ticket: Option<String> = None;
    let cap = re_ticket.captures(query.unwrap_or(""));
    let mut ticket = match &cap {
      None => None,
      Some(val) => {
        let t_now: &str = &val["ticket"];
        let t_now = t_now.to_string();
        Some(t_now)
      }
    };

    if ticket == None {
      let auth = req.headers().get("Authorization");
      ticket = match auth {
        Some(val) => {
          let ticket = val.to_str().unwrap();
          match ticket.len() > 7 {
            true => {
              let tmp_ticket = &ticket[7..];
              let tmp_ticket = tmp_ticket.to_string();
              Some(tmp_ticket)
            }
            false => None,
          }
        }
        None => None,
      }
    }
    // let ticket = ticket.to_string();
    let req_id = req.headers().get("RequestId");
    let request_id = match req_id {
      None => {
        let my_uuid = Uuid::new_v4();
        my_uuid.to_string()
      }
      Some(val) => {
        let req_id = val.to_str().unwrap();
        match req_id.len() > 0 {
          true => req_id.to_string(),
          false => {
            let my_uuid = Uuid::new_v4();
            my_uuid.to_string()
          }
        }
      }
    };

    // println!("ticket: {:?}\r\nrequest: {:?}", ticket, request_id);

    UserRequest {
      // req,
      ticket,
      request_id,
    }
  }

  pub fn ticket(&self) -> &Option<String> {
    &self.ticket
  }

  pub fn request_id(&self) -> &str {
    &self.request_id
  }
}
