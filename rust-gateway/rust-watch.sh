cargo build --all --features hot_reload_libs \
  && RUST_BACKTRACE=1 cargo watch -i "*/rust-gateway/**" -x "run --features hot_reload_libs"