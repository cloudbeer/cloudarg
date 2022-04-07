use once_cell::sync::OnceCell;
use std::fs;

static FAVICON: OnceCell<Vec<u8>> = OnceCell::new();

pub fn init() {
  let favicon = fs::read("./resources/logo.ico").expect("Wrong icon file [./resources/logo.ico].");
  FAVICON.set(favicon).expect("error favicon data.");
}

pub fn get() -> &'static Vec<u8> {
  FAVICON.get().expect("favicon not initialized.")
}
