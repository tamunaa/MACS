use std::iter::FromIterator;

struct Node<T> {
    data: T,
    next: Option<Box<Node<T>>>,
}

pub struct SimpleLinkedList<T> {
    // Delete this field
    // dummy is needed to avoid unused parameter error during compilation
    head: Option<Box<Node<T>>>,
    length: usize,
}

impl<T> SimpleLinkedList<T> {
    pub fn new() -> Self {
        SimpleLinkedList {
            head: None,
            length: 0,
        }
    }
    // You may be wondering why it's necessary to have is_empty()
    // when it can easily be determined from len().
    // It's good custom to have both because len() can be expensive for some types,
    // whereas is_empty() is almost always cheap.
    // (Also ask yourself whether len() is expensive for SimpleLinkedList)
    pub fn is_empty(&self) -> bool {
        if self.length == 0 {
            return true
        }
        return false;
    }

    pub fn len(&self) -> usize {
        return self.length;
    }

    pub fn push(&mut self, element: T) {
        let new_node = Box::new(Node {
            data: element,
            next: self.head.take(),
        });
        self.head = Some(new_node);
        self.length += 1;
    }


    pub fn pop(&mut self) -> Option<T> {
        if self.length == 0 {
            return None;
        }

        self.head.take().map(|node|{
            self.head = node.next;
            self.length -= 1;
            node.data
        })
    }

    pub fn peek(&self) -> Option<&T> {
        self.head.as_ref().map(|node| {
            &node.data
        })
    }

    #[must_use]
    pub fn rev(self) -> SimpleLinkedList<T> {
        let mut reversed = SimpleLinkedList::new();
        let mut current = self.head;

        while let Some(mut node) = current.take() {
            let next = node.next.take();
            reversed.push(node.data);
            current = next;
        }

        reversed
    }
}

impl<T> FromIterator<T> for SimpleLinkedList<T> {
    fn from_iter<I: IntoIterator<Item = T>>(iter: I) -> Self {
        let mut linked_list = SimpleLinkedList::new();
        for item in iter {
            linked_list.push(item);
        }
        linked_list
    }
}

// In general, it would be preferable to implement IntoIterator for SimpleLinkedList<T>
// instead of implementing an explicit conversion to a vector. This is because, together,
// FromIterator and IntoIterator enable conversion between arbitrary collections.
// Given that implementation, converting to a vector is trivial:
//
// let vec: Vec<_> = simple_linked_list.into_iter().collect();
//
// The reason this exercise's API includes an explicit conversion to Vec<T> instead
// of IntoIterator is that implementing that interface is fairly complicated, and
// demands more of the student than we expect at this point in the track.
impl<T> From<SimpleLinkedList<T>> for Vec<T> {
    fn from(mut linked_list: SimpleLinkedList<T>) -> Vec<T> {
        let mut vec = Vec::new();
        while let Some(element) = linked_list.pop() {
            vec.push(element);
        }
        vec.reverse();
        vec
    }
}