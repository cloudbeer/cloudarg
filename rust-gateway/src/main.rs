#![deny(warnings)]

use cloudarg::entities::{config, favicon};
use cloudarg::main_server;
use cloudarg::manage_server;

use std::thread;
use std::time::Duration;

#[tokio::main]
async fn main() {
    print!("\r\nLoading favicon...");
    favicon::init();
    println!("[done]");
    print!("Loading config...");
    config::init();
    println!("[done]");

    //加载 main server
    thread::spawn(|| {
        main_server::start();
    });
    thread::sleep(Duration::from_micros(1000));
    //加载 manage server
    thread::spawn(|| {
        manage_server::start();
    })
    .join()
    .expect("Thread panicked");
}
