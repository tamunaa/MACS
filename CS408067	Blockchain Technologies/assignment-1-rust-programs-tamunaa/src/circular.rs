pub struct CircularBuffer<T> where T: Clone {
    // We fake using T here, so the compiler does not complain that
    // "parameter `T` is never used". Delete when no longer needed.
    phantom: std::marker::PhantomData<T>,
    buffer: Vec<Option<T>>,
    capacity: usize,
    current_element_num: i32,
    start: usize,
    end: usize,
}

#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    EmptyBuffer,
    FullBuffer,
}

impl<T> CircularBuffer<T> where T: Clone {
    pub fn new(capacity: usize) -> Self {
        let buffer = vec![None; capacity];
        CircularBuffer {
            phantom: std::marker::PhantomData,
            buffer,
            capacity,
            current_element_num: 0,
            start: 0,
            end: 0,
        }
    }


    pub fn write(&mut self, _element: T) -> Result<(), Error> {
        if self.current_element_num == self.capacity as i32 {
            return Err::<_, _>(Error::FullBuffer);
        }

        self.buffer[self.end] = Some(_element);
        self.end += 1;
        self.end %= self.capacity;
        self.current_element_num += 1;

        return Ok(());
    }

    pub fn read(&mut self) -> Result<T, Error> {
        if self.current_element_num == 0 {
            return Err::<_, _>(Error::EmptyBuffer);
        }

        let element = self.buffer[self.start].clone();
        self.start += 1;
        self.start %= self.capacity;
        self.current_element_num -= 1;

        return Ok(element.unwrap());
    }

    pub fn clear(&mut self) {
        self.start = 0;
        self.end = 0;
        self.current_element_num = 0;
        self.buffer.clear();
        self.buffer.resize(self.capacity, None);
    }

    pub fn overwrite(&mut self, _element: T) {
       if self.current_element_num < self.capacity as i32 {
           self.write(_element);
       }else{
           self.buffer[self.start] = Some(_element);
           self.start += 1;
           self.start %= self.capacity;
       }
    }
}
