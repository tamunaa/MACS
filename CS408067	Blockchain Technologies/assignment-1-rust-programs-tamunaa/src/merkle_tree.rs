pub use sha256::digest;
use std::collections::VecDeque;
const CHILDREN_LENGTH: usize = 1024;
const DATA_ITEM_PREFIX: &str = "data item ";

#[derive(Debug)]
pub struct MerkleProof {
    pub root: String,
    pub proof_hashes: Vec<String>,
}

fn build_merkle_tree(data_items: Vec<&str>) -> Vec<String> {
    if data_items.is_empty() {
        return Vec::new();
    }

    let mut merkle_tree: Vec<String> = data_items.iter().map(|&item| digest(item).to_string()).collect();
    let mut current_level: VecDeque<String> = merkle_tree.iter().cloned().collect();

    while current_level.len() > 1 {
        let mut next_level = VecDeque::new();

        while !current_level.is_empty() {
            let left = current_level.pop_front().unwrap();
            let right = current_level.pop_front().unwrap_or_else(|| left.clone());
            
            let parent_node = digest(left + &right);
        
            next_level.push_back(parent_node.clone());
            merkle_tree.push(parent_node);
        }
        current_level = next_level;
    }

    merkle_tree
}

impl MerkleProof {
    pub fn generate_tree_components(initial_position: usize) -> MerkleProof {
        let mut data_items_tmp: Vec<String> = Vec::new();
        for i in 0..CHILDREN_LENGTH{
            let cur = format!("{}{}", DATA_ITEM_PREFIX, i);
            data_items_tmp.push(cur);
        }
        
        let data_items: Vec<&str> = (0..CHILDREN_LENGTH)
        .map(|i| data_items_tmp[i].as_str())
        .collect();

        let data_items_str: Vec<&str> = data_items.iter().map(|&item| item).collect(); // Convert to &str

        let merkle_tree = build_merkle_tree(data_items_str);

        let mut merkle_proof = MerkleProof {
            root: merkle_tree.last().unwrap().clone(),
            proof_hashes: Vec::new(),
        };

        let mut index_at_current_level = initial_position;
        // let mut index_at_current_level = 0; //debuuuuuuuuuuuuuuug
        let mut past_elem_num = 0;
        let mut elem_num = CHILDREN_LENGTH;


        while true {
            let complement_index = if index_at_current_level % 2 == 0 {
                index_at_current_level + 1
            } else {
                index_at_current_level - 1
            };
            
            merkle_proof.proof_hashes.push(
                merkle_tree[past_elem_num + complement_index].clone(),
            );

            index_at_current_level /= 2;
            past_elem_num += elem_num;
            elem_num/=2;

            if past_elem_num >= merkle_tree.len() - 1{
                break;
            }
        }

        merkle_proof
    }
}
