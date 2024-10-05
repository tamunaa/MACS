
fn is_yelling(message : &str) -> bool{
    let mut flag = false;

    for c in message.chars() {
        if !c.is_alphabetic() {
            continue;
        }

        if c.is_ascii_lowercase() {
            return false;
        }
        flag = true;
    }

    return flag;
}

fn is_question(message : &str) -> bool{
    let length = message.len();
    if length == 0 {
        return false;
    }
    let last_character = match message.chars().nth(length-1) {
        Some(c) => c,
        None => ' ',
    };
    if last_character == '?' {
        return true;
    }
    return false;
}

fn is_silence(message : &str) -> bool{
    for c in message.chars() {
        if !c.is_whitespace() {
            return false;
        }
    }
    return true;
}

pub fn reply(message: &str) -> &str {
    let trimmed = message.trim();
    let question = is_question(trimmed);
    let yelling = is_yelling(trimmed);
    let silence = is_silence(trimmed);

    return if silence {
        "Fine. Be that way!"
    } else if question && yelling {
        "Calm down, I know what I'm doing!"
    } else if question {
        "Sure."
    } else if yelling {
        "Whoa, chill out!"
    } else {
        "Whatever."
    }
}

