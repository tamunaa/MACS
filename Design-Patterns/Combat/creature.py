from random import choice, randint

from body_parts import (
    BodySelection,
    Claw,
    ClawDecorator,
    Leg,
    Teeth,
    TeethDecorator,
    Wing,
)
from movement import CrawlStrategy, FlyStrategy, HopStrategy, RunStrategy


class Creature:
    def __init__(self, name):
        self._position = 0
        self._name = name
        self._health = 100
        self._stamina = 100
        self._body = BodySelection()
        self._movement_strategy = CrawlStrategy()

    @property
    def attack_power(self):
        return self._body.attack_power

    @property
    def name(self):
        return self._name

    def move(self, distance):
        self._position += distance

    @property
    def position(self):
        return self._position

    @position.setter
    def position(self, value):
        self._position = value

    def add_body_part(self, body_part):
        self._body.add_part(body_part)
        self._select_best_possible_movement_strategy()

    def get_best_possible_distance(self):
        self._select_best_possible_movement_strategy()
        if self._stamina > self._movement_strategy.stamina_required():
            distance = self._movement_strategy.move()
            return distance
        else:
            # no stamina man
            return False

    def _select_best_possible_movement_strategy(self):
        legs = sum(1 for part in self._body._parts if isinstance(part, Leg))
        wings = sum(1 for part in self._body._parts if isinstance(part, Wing))

        if wings >= 2 and self._stamina >= FlyStrategy.stamina_required():
            self._movement_strategy = FlyStrategy()
        elif legs >= 2 and self._stamina >= RunStrategy.stamina_required():
            self._movement_strategy = RunStrategy()
        elif legs >= 1 and self._stamina >= HopStrategy.stamina_required():
            self._movement_strategy = HopStrategy()
        elif self._stamina >= CrawlStrategy.stamina_required():
            self._movement_strategy = CrawlStrategy()


class CreatureFactory:
    @classmethod
    def create_predator(cls) -> Creature:
        predator = Creature("Predator")
        random_body = cls._get_random_body()
        for part in random_body:
            predator.add_body_part(part)
        return predator

    @classmethod
    def create_prey(cls, max_position: int) -> Creature:
        prey = Creature("Prey")
        prey.position = randint(0, max_position)
        random_body = cls._get_random_body()
        for part in random_body:
            prey.add_body_part(part)
        return prey

    @classmethod
    def _get_random_body(cls):
        available_parts = [
            Wing(),
            Wing(),
            Leg(),
            Leg(),
            ClawDecorator(Claw(), choice(["small", "medium", "big"])),
            TeethDecorator(Teeth(), choice(["low", "medium", "high"])),
        ]
        # now, creature might not have any attacking parts
        # but that's okay during evolution phase
        # another possible idea would be choosing some moving and attacking parts seperately

        body = []
        for _ in range(randint(2, 5)):
            body.append(choice(available_parts))
        return body
