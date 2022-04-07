use crate::entities::favicon;

use std::convert::Infallible;
use std::net::SocketAddr;

use hyper::service::{make_service_fn, service_fn};
use hyper::{Body, Method, Request, Response, Server};

#[tokio::main]
pub async fn start() {
  let addr = SocketAddr::from(([0, 0, 0, 0], 18089));

  let make_service =
    make_service_fn(move |_| async move { Ok::<_, Infallible>(service_fn(move |req| proxy(req))) });

  let server = Server::bind(&addr)
    .http1_preserve_header_case(true)
    .http1_title_case_headers(true)
    .serve(make_service);
  println!("\r\nCloudarg-man listening on http://{} ", addr);
  if let Err(e) = server.await {
    eprintln!("server error: {}", e);
  }
}

async fn proxy(req: Request<Body>) -> Result<Response<Body>, hyper::Error> {
  let path = req.uri().path();
  let method = req.method();

  println!("{method}: {path}");
  match (method, path) {
    (&Method::GET, "/") => Ok(Response::new(Body::from("Cloudarg manage api."))),
    (&Method::GET, "/favicon.ico") => {
      let vec_favicon = favicon::get();
      let builder = Response::builder()
        .header("content-type", "image/x-icon")
        .body(Body::from(&vec_favicon[..]))
        .expect("Error create body");
      Ok(builder)
    }
    (&Method::GET, "/metrics") => Ok(Response::new(Body::from("Under building..."))),
    _ => Ok(Response::new(Body::from("Not found..."))),
  }
}
