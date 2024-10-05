from abc import ABC, abstractmethod


class MovementStrategy(ABC):
    @abstractmethod
    def move(self) -> int:
        pass

    @abstractmethod
    def stamina_used(self) -> int:
        pass

    @abstractmethod
    def stamina_required(self) -> int:
        pass


class CrawlStrategy(MovementStrategy):
    def move(self):
        return 1

    @classmethod
    def stamina_used(cls):
        return 1

    @classmethod
    def stamina_required(cls):
        return 0


class HopStrategy(MovementStrategy):
    def move(self):
        return 3

    @classmethod
    def stamina_used(cls):
        return 2

    @classmethod
    def stamina_required(cls):
        return 20


class WalkStrategy(MovementStrategy):
    def move(self):
        return 4

    @classmethod
    def stamina_used(cls):
        return 2

    @classmethod
    def stamina_required(cls):
        return 40


class RunStrategy(MovementStrategy):
    def move(self):
        return 6

    @classmethod
    def stamina_used(cls):
        return 4

    @classmethod
    def stamina_required(cls):
        return 60


class FlyStrategy(MovementStrategy):
    def move(self):
        return 8

    @classmethod
    def stamina_used(cls):
        return 4

    @classmethod
    def stamina_required(cls):
        return 80
