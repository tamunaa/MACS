import logging
from abc import abstractmethod, ABC
from typing import Tuple
from creature import Creature, CreatureFactory


class Phase(ABC):
    @abstractmethod
    def execute(self, predator: Creature, prey: Creature):
        pass


class EvolutionPhase(Phase):
    def execute(self, predator: Creature, prey: Creature) -> Tuple[Creature, Creature]:
        predator = CreatureFactory.create_predator()
        prey = CreatureFactory.create_prey(1000)

        logging.info(
            f"Predator evolved at position {predator.position} with attack power {predator.attack_power}"
        )
        logging.info(
            f"Prey evolved at position {prey.position} with attack power {prey.attack_power}"
        )

        return predator, prey


class ChasePhase(Phase):
    def execute(self, predator: Creature, prey: Creature) -> bool:
        rounds = 0
        max_rounds = 1000  # Prevent infinite loops

        while rounds < max_rounds:
            # Prey moves first
            prey_distance = prey.get_best_possible_distance()
            if prey_distance:
                prey.move(prey_distance)
                prey._stamina -= prey._movement_strategy.stamina_used()
                logging.info(
                    f"Prey moved to position {prey.position}, stamina: {prey._stamina}"
                )

            predator_distance = predator.get_best_possible_distance()
            if not predator_distance:
                logging.info("Pray ran into infinity (Predator out of stamina)")
                return False

            actual_distance = min(
                predator_distance, abs(predator.position - prey.position)
            )
            predator.move(actual_distance)
            predator._stamina -= predator._movement_strategy.stamina_used()
            logging.info(
                f"Predator moved to position {predator.position}, stamina: {predator._stamina}"
            )

            if abs(predator.position - prey.position) <= 1:
                logging.info("Predator caught prey, starting fight phase")
                return True

            rounds += 1

        logging.info("Chase exceeded maximum rounds, prey escapes")
        return False


class FightPhase(Phase):
    def execute(self, predator: Creature, prey: Creature) -> bool:
        while True:
            # Predator attacks first
            prey._health -= predator.attack_power
            logging.info(f"Predator attacks! Prey health: {prey._health}")

            if prey._health <= 0:
                logging.info("Some R-rated things have happened")
                return True

            # Prey counterattacks
            predator._health -= prey.attack_power
            logging.info(f"Prey counterattacks! Predator health: {predator._health}")

            if predator._health <= 0:
                logging.info("Pray ran into infinity (Predator died)")
                return False


class Simulation:
    def __init__(self):
        self.evolution_phase = EvolutionPhase()
        self.chase_phase = ChasePhase()
        self.fight_phase = FightPhase()

    def run_single_simulation(self) -> bool:
        """Runs a single simulation and returns True if predator wins, False if prey escapes/wins"""
        predator = None
        prey = None

        # Evolution Phase
        predator, prey = self.evolution_phase.execute(predator, prey)

        # Chase Phase
        if not self.chase_phase.execute(predator, prey):
            return False

        # Fight Phase
        return self.fight_phase.execute(predator, prey)

    def run_multiple_simulations(self, count: int = 100) -> dict:
        """Runs multiple simulations and returns statistics"""

        results = {"predator_wins": 0, "prey_escapes": 0, "total_simulations": count}

        for i in range(count):
            logging.info(f"\nStarting simulation {i + 1}")
            if self.run_single_simulation():
                results["predator_wins"] += 1
            else:
                results["prey_escapes"] += 1

        return results
