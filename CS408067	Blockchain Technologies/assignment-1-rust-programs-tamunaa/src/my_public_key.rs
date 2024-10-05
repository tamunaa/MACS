// extern crate num_bigint;
// use num_bigint::BigUint;
// use num_traits::FromPrimitive;

pub struct MyPublicKey {
    data: [u8; 32],
    encoded_string: Option<String>,
}

const ALPHABET: &str = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

impl MyPublicKey { pub fn new(data: [u8; 32], encoded_string: Option<String>) -> Self {
        MyPublicKey {
            data,
            encoded_string,
        }
    }
}
impl From<&str> for MyPublicKey {
    fn from(value: &str) -> Self {
        let data = decode_58(value);
        MyPublicKey::new(data, Some(value.to_string()))
    }
}

impl From<String> for MyPublicKey {
    fn from(value: String) -> Self {
        Self::from(value.as_str())
    }
}

impl From<[u8; 32]> for MyPublicKey {
    fn from(bytes: [u8; 32]) -> Self {
        MyPublicKey::new(bytes, None)
    }
}

impl PartialEq for MyPublicKey {
    fn eq(&self, other: &Self) -> bool {
        self.data == other.data
    }
}
fn decode_58(encoded: &str) -> [u8; 32] {
    if encoded.is_empty() {
        panic!("Invalid character in base58 string: {}", encoded);
    }
    const BASE58_ALPHABET: &[u8; 58] = b"123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    let mut result: [u8; 32] = [0; 32];

    for c in encoded.chars() {
        let mut value: u64 = 0;
        let mut found = false;

        for (idx, &char_value) in BASE58_ALPHABET.iter().enumerate() {
            if c as u8 == char_value {
                value = idx as u64;
                found = true;
                break;
            }
        }

        if !found {
            panic!("Invalid character in base58 string: {}", c);
        }

        for i in (0..32).rev() {
            value += u64::from(result[i]) * 58;
            result[i] = (value & 0xFF) as u8;
            value >>= 8;
        }
    }
    result
}

