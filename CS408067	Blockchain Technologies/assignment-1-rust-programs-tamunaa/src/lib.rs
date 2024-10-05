mod bob;
mod circular;
mod linked_list;
mod my_public_key;
mod merkle_tree;

#[cfg(test)]
mod bob_tests {
    use super::bob::*;

    fn process_response_case(phrase: &str, expected_response: &str) {
        assert_eq!(reply(phrase), expected_response);
    }
    
    #[test]
    /// stating something
    fn test_stating_something() {
        process_response_case("Tom-ay-to, tom-aaaah-to.", "Whatever.");
    }
    
    #[test]
    /// ending with whitespace
    fn test_ending_with_whitespace() {
        process_response_case("Okay if like my  spacebar  quite a bit?   ", "Sure.");
    }
    
    #[test]
    /// shouting numbers
    fn test_shouting_numbers() {
        process_response_case("1, 2, 3 GO!", "Whoa, chill out!");
    }
    
    #[test]
    /// other whitespace
    fn test_other_whitespace() {
        process_response_case("\r\r 	", "Fine. Be that way!");
    }
    
    #[test]
    /// shouting with special characters
    fn test_shouting_with_special_characters() {
        process_response_case(
            "ZOMG THE %^*@#$(*^ ZOMBIES ARE COMING!!11!!1!",
            "Whoa, chill out!",
        );
    }
    
    #[test]
    /// talking forcefully
    fn test_talking_forcefully() {
        process_response_case("Hi there!", "Whatever.");
    }
    
    #[test]
    /// prattling on
    fn test_prattling_on() {
        process_response_case("Wait! Hang on. Are you going to be OK?", "Sure.");
    }
    
    #[test]
    /// forceful question
    fn test_forceful_question() {
        process_response_case("WHAT'S GOING ON?", "Calm down, I know what I'm doing!");
    }
    
    #[test]
    /// shouting with no exclamation mark
    fn test_shouting_with_no_exclamation_mark() {
        process_response_case("I HATE THE DENTIST", "Whoa, chill out!");
    }
    
    #[test]
    /// asking gibberish
    fn test_asking_gibberish() {
        process_response_case("fffbbcbeab?", "Sure.");
    }
    
    #[test]
    /// question with no letters
    fn test_question_with_no_letters() {
        process_response_case("4?", "Sure.");
    }
    
    #[test]
    /// no letters
    fn test_no_letters() {
        process_response_case("1, 2, 3", "Whatever.");
    }
    
    #[test]
    /// statement containing question mark
    fn test_statement_containing_question_mark() {
        process_response_case("Ending with ? means a question.", "Whatever.");
    }
    
    //NEW
    #[test]
    /// multiple line question
    fn test_multiple_line_question() {
        process_response_case(
            "\rDoes this cryogenic chamber make me look fat?\rNo.",
            "Whatever.",
        );
    }
    
    #[test]
    /// non-question ending with whitespace
    fn test_nonquestion_ending_with_whitespace() {
        process_response_case(
            "This is a statement ending with whitespace      ",
            "Whatever.",
        );
    }
    
    #[test]
    /// shouting
    fn test_shouting() {
        process_response_case("WATCH OUT!", "Whoa, chill out!");
    }
    
    #[test]
    /// non-letters with question
    fn test_nonletters_with_question() {
        process_response_case(":) ?", "Sure.");
    }
    
    #[test]
    /// shouting gibberish
    fn test_shouting_gibberish() {
        process_response_case("FCECDFCAAB", "Whoa, chill out!");
    }
    
    #[test]
    /// asking a question
    fn test_asking_a_question() {
        process_response_case("Does this cryogenic chamber make me look fat?", "Sure.");
    }
    
    #[test]
    /// asking a numeric question
    fn test_asking_a_numeric_question() {
        process_response_case("You are, what, like 15?", "Sure.");
    }
    
    #[test]
    /// silence
    fn test_silence() {
        process_response_case("", "Fine. Be that way!");
    }
    
    #[test]
    /// starting with whitespace
    fn test_starting_with_whitespace() {
        process_response_case("         hmmmmmmm...", "Whatever.");
    }
    
    #[test]
    /// using acronyms in regular speech
    fn test_using_acronyms_in_regular_speech() {
        process_response_case(
            "It's OK if you don't want to go work for NASA.",
            "Whatever.",
        );
    }
    
    #[test]
    /// alternate silence
    fn test_alternate_silence() {
        process_response_case("										", "Fine. Be that way!");
    }
    
    #[test]
    /// prolonged silence
    fn test_prolonged_silence() {
        process_response_case("          ", "Fine. Be that way!");
    }
    
}

#[cfg(test)]
mod circular_tests {
    use super::circular::*;
    use std::rc::Rc;
    
    #[test]
    fn error_on_read_empty_buffer() {
        let mut buffer = CircularBuffer::<char>::new(1);
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn can_read_item_just_written() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        assert_eq!(Ok('1'), buffer.read());
    }
    
    #[test]
    fn each_item_may_only_be_read_once() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        assert_eq!(Ok('1'), buffer.read());
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn items_are_read_in_the_order_they_are_written() {
        let mut buffer = CircularBuffer::new(2);
        assert!(buffer.write('1').is_ok());
        assert!(buffer.write('2').is_ok());
        assert_eq!(Ok('1'), buffer.read());
        assert_eq!(Ok('2'), buffer.read());
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn full_buffer_cant_be_written_to() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        assert_eq!(Err(Error::FullBuffer), buffer.write('2'));
    }
    
    #[test]
    fn read_frees_up_capacity_for_another_write() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        assert_eq!(Ok('1'), buffer.read());
        assert!(buffer.write('2').is_ok());
        assert_eq!(Ok('2'), buffer.read());
    }
    
    #[test]
    fn read_position_is_maintained_even_across_multiple_writes() {
        let mut buffer = CircularBuffer::new(3);
        assert!(buffer.write('1').is_ok());
        assert!(buffer.write('2').is_ok());
        assert_eq!(Ok('1'), buffer.read());
        assert!(buffer.write('3').is_ok());
        assert_eq!(Ok('2'), buffer.read());
        assert_eq!(Ok('3'), buffer.read());
    }
    
    #[test]
    fn items_cleared_out_of_buffer_cant_be_read() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        buffer.clear();
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn clear_frees_up_capacity_for_another_write() {
        let mut buffer = CircularBuffer::new(1);
        assert!(buffer.write('1').is_ok());
        buffer.clear();
        assert!(buffer.write('2').is_ok());
        assert_eq!(Ok('2'), buffer.read());
    }
    
    #[test]
    fn clear_does_nothing_on_empty_buffer() {
        let mut buffer = CircularBuffer::new(1);
        buffer.clear();
        assert!(buffer.write('1').is_ok());
        assert_eq!(Ok('1'), buffer.read());
    }
    
    #[test]
    fn clear_actually_frees_up_its_elements() {
        let mut buffer = CircularBuffer::new(1);
        let element = Rc::new(());
        assert!(buffer.write(Rc::clone(&element)).is_ok());
        assert_eq!(Rc::strong_count(&element), 2);
        buffer.clear();
        assert_eq!(Rc::strong_count(&element), 1);
    }
    
    #[test]
    fn overwrite_acts_like_write_on_non_full_buffer() {
        let mut buffer = CircularBuffer::new(2);
        assert!(buffer.write('1').is_ok());
        buffer.overwrite('2');
        assert_eq!(Ok('1'), buffer.read());
        assert_eq!(Ok('2'), buffer.read());
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn overwrite_replaces_the_oldest_item_on_full_buffer() {
        let mut buffer = CircularBuffer::new(2);
        assert!(buffer.write('1').is_ok());
        assert!(buffer.write('2').is_ok());
        buffer.overwrite('A');
        assert_eq!(Ok('2'), buffer.read());
        assert_eq!(Ok('A'), buffer.read());
    }
    
    #[test]
    fn overwrite_replaces_the_oldest_item_remaining_in_buffer_following_a_read() {
        let mut buffer = CircularBuffer::new(3);
        assert!(buffer.write('1').is_ok());
        assert!(buffer.write('2').is_ok());
        assert!(buffer.write('3').is_ok());
        assert_eq!(Ok('1'), buffer.read());
        assert!(buffer.write('4').is_ok());
        buffer.overwrite('5');
        assert_eq!(Ok('3'), buffer.read());
        assert_eq!(Ok('4'), buffer.read());
        assert_eq!(Ok('5'), buffer.read());
    }
    
    #[test]
    fn integer_buffer() {
        let mut buffer = CircularBuffer::new(2);
        assert!(buffer.write(1).is_ok());
        assert!(buffer.write(2).is_ok());
        assert_eq!(Ok(1), buffer.read());
        assert!(buffer.write(-1).is_ok());
        assert_eq!(Ok(2), buffer.read());
        assert_eq!(Ok(-1), buffer.read());
        assert_eq!(Err(Error::EmptyBuffer), buffer.read());
    }
    
    #[test]
    fn string_buffer() {
        let mut buffer = CircularBuffer::new(2);
        buffer.write("".to_string()).unwrap();
        buffer.write("Testing".to_string()).unwrap();
        assert_eq!(0, buffer.read().unwrap().len());
        assert_eq!(Ok("Testing".to_string()), buffer.read());
    }
    
}

#[cfg(test)]
mod linked_list_tests {
    use super::linked_list::*;

    #[test]
    fn test_new_list_is_empty() {
        let list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        assert_eq!(list.len(), 0, "list's length must be 0");
    }
    
    #[test]
    fn test_push_increments_length() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        list.push(1);
        assert_eq!(list.len(), 1, "list's length must be 1");
        list.push(2);
        assert_eq!(list.len(), 2, "list's length must be 2");
    }
    
    #[test]
    fn test_pop_decrements_length() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        list.push(1);
        list.push(2);
        list.pop();
        assert_eq!(list.len(), 1, "list's length must be 1");
        list.pop();
        assert_eq!(list.len(), 0, "list's length must be 0");
    }
    
    #[test]
    #[ignore]
    fn test_is_empty() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        assert!(list.is_empty(), "List wasn't empty on creation");
        for inserts in 0..100 {
            for i in 0..inserts {
                list.push(i);
                assert!(
                    !list.is_empty(),
                    "List was empty after having inserted {i}/{inserts} elements"
                );
            }
            for i in 0..inserts {
                assert!(
                    !list.is_empty(),
                    "List was empty before removing {i}/{inserts} elements"
                );
                list.pop();
            }
            assert!(
                list.is_empty(),
                "List wasn't empty after having removed {inserts} elements"
            );
        }
    }
    
    #[test]
    fn test_pop_returns_head_element_and_removes_it() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        list.push(1);
        list.push(2);
        assert_eq!(list.pop(), Some(2), "Element must be 2");
        assert_eq!(list.pop(), Some(1), "Element must be 1");
        assert_eq!(list.pop(), None, "No element should be contained in list");
    }
    
    #[test]
    fn test_peek_returns_reference_to_head_element_but_does_not_remove_it() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        assert_eq!(list.peek(), None, "No element should be contained in list");
        list.push(2);
        assert_eq!(list.peek(), Some(&2), "Element must be 2");
        assert_eq!(list.peek(), Some(&2), "Element must be still 2");
        list.push(3);
        assert_eq!(list.peek(), Some(&3), "Head element is now 3");
        assert_eq!(list.pop(), Some(3), "Element must be 3");
        assert_eq!(list.peek(), Some(&2), "Head element is now 2");
        assert_eq!(list.pop(), Some(2), "Element must be 2");
        assert_eq!(list.peek(), None, "No element should be contained in list");
    }
    
    #[test]
    fn test_from_slice() {
        let mut array = vec!["1", "2", "3", "4"];
        let mut list: SimpleLinkedList<_> = array.drain(..).collect();
        assert_eq!(list.pop(), Some("4"));
        assert_eq!(list.pop(), Some("3"));
        assert_eq!(list.pop(), Some("2"));
        assert_eq!(list.pop(), Some("1"));
    }
    
    #[test]
    fn test_reverse() {
        let mut list: SimpleLinkedList<u32> = SimpleLinkedList::new();
        list.push(1);
        list.push(2);
        list.push(3);
        let mut rev_list = list.rev();
        assert_eq!(rev_list.pop(), Some(1));
        assert_eq!(rev_list.pop(), Some(2));
        assert_eq!(rev_list.pop(), Some(3));
        assert_eq!(rev_list.pop(), None);
    }
    
    #[test]
    fn test_into_vector() {
        let mut v = Vec::new();
        let mut s = SimpleLinkedList::new();
        for i in 1..4 {
            v.push(i);
            s.push(i);
        }
        let s_as_vec: Vec<i32> = s.into();
        assert_eq!(v, s_as_vec);
    }
}


#[cfg(test)]
mod my_pubkey_tests {
    use super::my_public_key::*;

    #[test]
    #[should_panic]
    fn invalid_input_length_from_bs58_str() {
        let pubkey = MyPublicKey::from("");
    }
    
    #[test]
    #[should_panic]
    fn invalid_characters_from_bs58_str() {
        let pubkey = MyPublicKey::from("29zMU3FKQt7cSgfUK1rneDUc82R1hVPHoYfkhWa9L1uI");
    }
    
    #[test]
    fn equal_pubkeys() {
        let pubkey_from_str = MyPublicKey::from("29zMU3FKQt7cSgfUK1rneDUc82R1hVPHoYfkhWa9L1uc");
        let pubkey_from_bytes = MyPublicKey::from([
            17,  41,  46, 183, 177, 160, 230, 126,
            40,  46, 102,  50,  85, 227, 103, 112,
            53, 159,  44,  48, 178, 147, 216, 239,
            83, 143,  70,  37, 180, 129, 219,  67
          ]);
    
          assert_eq!(pubkey_from_str == pubkey_from_bytes, true);
    }
    
    #[test]
    fn equality_leading_zeroes() {
        let pubkey_from_str = MyPublicKey::from("111111111111111111111111111111PA");
        let pubkey_from_bytes = MyPublicKey::from([
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            5, 5
        ]);
    
        assert_eq!(pubkey_from_str == pubkey_from_bytes, true);
    }
    
}

#[cfg(test)]
mod merkle_tree_tests {
    use super::merkle_tree::*;

    #[test]
    fn test1() {

        for i in 1..100 {
            let pos = i * 10;

            let proof = MerkleProof::generate_tree_components(pos as usize);

            assert_eq!(proof.root, compute_root(pos, &proof));
        }
    }

    pub fn compute_root(initial_position: i32, merkle_proof: &MerkleProof) -> String {
        let mut padded_value = format!("data item {}", initial_position);
        
        let mut current_hash_str = digest(padded_value);
        let mut pos = initial_position;

        for hash_from_proof in merkle_proof.proof_hashes.iter() {
            let mut combined: String = String::new();

            if pos % 2 == 0 {
                combined.push_str(&current_hash_str);
                combined.push_str(hash_from_proof);
            } else {
                combined.push_str(hash_from_proof);
                combined.push_str(&current_hash_str);
            }
            
            current_hash_str = digest(combined);

            pos = pos >> 1;
            
        }

        let root = current_hash_str;
        return root;
    }
}
