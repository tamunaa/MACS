from abc import ABC


class BodyPart(ABC):
    @property
    def attack_power(self):
        return 0

    @property
    def movement_contribution(self):
        return 0


class Leg(BodyPart):
    @property
    def movement_contribution(self):
        return 1


class Wing(BodyPart):
    @property
    def movement_contribution(self):
        return 1


class Claw(BodyPart):
    @property
    def attack_power(self):
        return 5


class Teeth(BodyPart):
    @property
    def attack_power(self):
        return 4


class ClawDecorator(BodyPart):
    def __init__(self, body_part: BodyPart, size: str):
        self._decorated_part = body_part
        self._size = size

    @property
    def attack_power(self):
        multiplier = {"small": 2, "medium": 3, "big": 4}.get(self._size, 1)
        return self._decorated_part.attack_power * multiplier


class TeethDecorator(BodyPart):
    def __init__(self, body_part: BodyPart, sharpness: str):
        self._decorated_part = body_part
        self.sharpness = sharpness

    @property
    def attack_power(self):
        boost = {"low": 3, "medium": 4, "high": 5}.get(self.sharpness, 0)
        return self._decorated_part.attack_power + boost


class BodySelection(BodyPart):
    def __init__(self):
        self._parts = []

    @property
    def attack_power(self):
        return sum(part.attack_power for part in self._parts)

    @property
    def movement_contribution(self):
        return sum(part.movement_contribution for part in self._parts)

    def add_part(self, part):
        self._parts.append(part)

    def remove_part(self, part):
        self._parts.remove(part)
