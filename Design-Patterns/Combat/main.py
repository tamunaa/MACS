import logging

from phases import Simulation

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    logging.log(logging.INFO, "Starting simulation")
    Simulation().run_single_simulation()
