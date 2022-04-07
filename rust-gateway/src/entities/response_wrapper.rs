use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
pub struct Response<T, E> {
  success: bool,
  #[allow(unused)]
  data: Option<T>,
  #[allow(unused)]
  error: Option<E>,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Error {
  message: String,
  code: isize,
}
