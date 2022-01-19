// #![deny(warnings)]

mod config;
mod favicon;

use hyper_tls::HttpsConnector;
use std::convert::Infallible;
use std::env;
use std::net::SocketAddr;

use hyper::service::{make_service_fn, service_fn};
use hyper::{Body, Client, Method, Request, Response, Server};

type HttpClient = Client<hyper_tls::HttpsConnector<hyper::client::HttpConnector>>;

#[tokio::main]
async fn main() {
    print!("\r\nLoading favicon...");
    favicon::init();
    println!("[done]");
    print!("Loading config...");
    config::init();
    let config_ins = config::get();
    println!("[done]");

    let https = HttpsConnector::new();

    let client = Client::builder()
        .http1_title_case_headers(true)
        .http1_preserve_header_case(true)
        .build::<_, hyper::Body>(https);
    // .build_http();

    let make_service = make_service_fn(move |_| {
        let client = client.clone();
        async move { Ok::<_, Infallible>(service_fn(move |req| proxy(client.clone(), req))) }
    });

    let addr = SocketAddr::from(([0, 0, 0, 0], config_ins.port));
    println!(
        "\r\n\r\n::: {} ::: powered by  🔽 🔽 🔽 🔽 🔽 \r\n\
            {}\r\n\r\n\
            Version: {}\r\n\r\n\
            Cloudarg listening on http://{} ",
        config_ins.name,
        config_ins.description,
        env!("CARGO_PKG_VERSION"),
        addr
    );

    let server = Server::bind(&addr)
        // .http1_preserve_header_case(true)
        // .http1_title_case_headers(true)
        .serve(make_service);

    if let Err(e) = server.await {
        eprintln!("server error: {}", e);
    }
}

async fn proxy(client: HttpClient, req: Request<Body>) -> Result<Response<Body>, hyper::Error> {
    let path = req.uri().path();
    match path {
        "/favicon.ico" => {
            let vec_favicon = favicon::get();
            let builder = Response::builder()
                .header("content-type", "image/x-icon")
                .body(Body::from(&vec_favicon[..]))
                .expect("Error create body");
            Ok(builder)
        }
        "/metrics" => Ok(Response::new(Body::from("Under building..."))),
        _ => {
            println!("Other route... {:?}", path);
            let url = "https://httpbin.org/post";
            let req_new = Request::builder()
                .method(Method::POST)
                .uri(url)
                .header("content-type", "application/x-www-form-urlencoded")
                // .body(Body::from(r#"{"library":"hyper"}"#))
                .body(Body::from(
                    r#"action=TrainFace&database=politicians&identifier=president"#,
                ))
                // .body(Body::empty())
                .unwrap();

            client.request(req_new).await
        }
    }
}
