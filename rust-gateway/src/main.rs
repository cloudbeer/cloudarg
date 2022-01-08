use rand::Rng;
use std::cmp::Ordering;
use std::io;

fn main() {
    const THREE_HOURS_IN_SECONDS: u32 = 60 * 60 * 3;
    let x = b'A';

    let x = {
        let x = 3;
        x + 1
    };
    println!(
        "Hello, world! 你好，世界。{} - {}",
        THREE_HOURS_IN_SECONDS, x
    );
    return;

    let secret_number = rand::thread_rng().gen_range(1..101);
    // println!("秘密数字是：{}", secret_number);

    println!("猜下我的数字？");
    loop {
        let mut guess = String::new();
        match io::stdin().read_line(&mut guess) {
            Ok(_) => {
                // println!("输入完成，你输入了 {} 个字节", n);
                // println!("输入的是：{}", guess);
            }
            Err(_) => {
                println!("兄弟，你错误了,123。");
            }
        }
        let guess: u32 = match guess.trim().parse() {
            Ok(num) => num,
            Err(_) => {
                println!("输入错误, 请继续猜：");
                continue;
            }
        };
        match guess.cmp(&secret_number) {
            Ordering::Less => println!("太小了，请继续猜："),
            Ordering::Equal => {
                println!("Bingo");
                break;
            }
            Ordering::Greater => println!("太大了，请继续猜："),
        }
    }
    println!("搞定");

    // println!("你输入的是{}", guess);
}
