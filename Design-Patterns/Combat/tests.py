import pytest
from body_parts import (
    BodySelection,
    Claw,
    Leg,
    Teeth,
    Wing,
    ClawDecorator,
    TeethDecorator,
)
from movement import CrawlStrategy, FlyStrategy, HopStrategy, RunStrategy
from main import Creature
from phases import EvolutionPhase, ChasePhase, FightPhase, Simulation


@pytest.fixture
def basic_predator():
    predator = Creature("Predator")
    predator.add_body_part(Leg())
    predator.add_body_part(Leg())
    predator.add_body_part(ClawDecorator(Claw(), "big"))
    return predator


@pytest.fixture
def basic_prey():
    prey = Creature("Prey")
    prey.add_body_part(Wing())
    prey.add_body_part(Wing())
    prey._position = 10
    return prey


class TestCreature:
    def test_creature_initialization(self):
        creature = Creature("Test")
        assert creature.name == "Test"
        assert creature._health == 100
        assert creature._stamina == 100
        assert creature.position == 0

    def test_movement_strategy_selection(self):
        creature = Creature("Test")

        # Test crawling (default)
        assert isinstance(creature._movement_strategy, CrawlStrategy)

        # Test hopping
        creature.add_body_part(Leg())
        creature._select_best_possible_movement_strategy()
        assert isinstance(creature._movement_strategy, HopStrategy)

        # Test running
        creature.add_body_part(Leg())
        creature._select_best_possible_movement_strategy()
        assert isinstance(creature._movement_strategy, RunStrategy)

        # Test flying
        creature = Creature("Test")
        creature.add_body_part(Wing())
        creature.add_body_part(Wing())
        creature._select_best_possible_movement_strategy()
        assert isinstance(creature._movement_strategy, FlyStrategy)


class TestBodySelection:
    def test_empty_body(self):
        body = BodySelection()
        assert body.attack_power == 0
        assert body.movement_contribution == 0

    def test_single_part_body(self):
        body = BodySelection()
        body.add_part(Leg())
        assert body.attack_power == 0
        assert body.movement_contribution == 1

    def test_multiple_parts_body(self):
        body = BodySelection()
        body.add_part(Leg())
        body.add_part(Wing())
        body.add_part(Claw())
        assert body.attack_power == 5  # Only from Claw
        assert body.movement_contribution == 2  # From Leg and Wing

    def test_decorated_parts_body(self):
        body = BodySelection()
        body.add_part(Leg())
        body.add_part(Wing())
        body.add_part(ClawDecorator(Claw(), "big"))
        body.add_part(TeethDecorator(Teeth(), "high"))

        expected_attack = (5 * 4) + (4 + 5)  # Big claw (5*4) + High teeth (4+5)
        expected_movement = 2  # From Leg and Wing

        assert body.attack_power == expected_attack
        assert body.movement_contribution == expected_movement

    def test_part_removal(self):
        body = BodySelection()
        leg = Leg()
        wing = Wing()

        body.add_part(leg)
        body.add_part(wing)
        assert body.movement_contribution == 2

        body.remove_part(leg)
        assert body.movement_contribution == 1

    def test_complex_body_configuration(self):
        body = BodySelection()

        body.add_part(Leg())
        body.add_part(Leg())
        body.add_part(Wing())
        body.add_part(Wing())

        body.add_part(ClawDecorator(Claw(), "big"))
        body.add_part(TeethDecorator(Teeth(), "high"))

        expected_attack = (5 * 4) + (4 + 5)  # Big claw (5*4) + High teeth (4+5)
        expected_movement = 4  # From 2 Legs and 2 Wings

        assert body.attack_power == expected_attack
        assert body.movement_contribution == expected_movement

    def test_body_parts_independence(self):
        body = BodySelection()

        claw = ClawDecorator(Claw(), "big")
        body.add_part(claw)
        initial_attack = body.attack_power

        body.add_part(Leg())
        body.add_part(Wing())

        assert body.attack_power == initial_attack
        assert body.movement_contribution == 2


class TestAttackPower:
    def test_basic_prey_attack_power(self, basic_prey):
        assert basic_prey.attack_power == 0

    def test_basic_predator_attack_power(self, basic_predator):
        assert basic_predator.attack_power == 20


class TestPhases:
    def test_evolution_phase(self):
        phase = EvolutionPhase()
        predator, prey = phase.execute(None, None)
        print("creatures", predator, prey)

        assert isinstance(predator, Creature)
        assert isinstance(prey, Creature)
        assert predator.position == 0
        assert 0 <= prey.position <= 1000

    def test_chase_phase_predator_catches_prey(self, basic_predator, basic_prey):
        phase = ChasePhase()
        basic_predator._stamina = 100
        basic_prey._stamina = 20  # Limited stamina to ensure catch

        result = phase.execute(basic_predator, basic_prey)
        assert result is True

    def test_chase_phase_predator_runs_out_of_stamina(self, basic_predator, basic_prey):
        phase = ChasePhase()
        basic_predator._stamina = 10  # Very low stamina
        basic_prey._stamina = 100

        result = phase.execute(basic_predator, basic_prey)
        assert result is False

    def test_fight_phase_predator_wins(self, basic_predator, basic_prey):
        phase = FightPhase()
        basic_predator._health = 100
        basic_prey._health = 20

        result = phase.execute(basic_predator, basic_prey)
        assert result is True

    def test_fight_phase_prey_wins(self, basic_predator, basic_prey):
        phase = FightPhase()
        basic_predator._health = 0
        basic_predator._stamina = 0
        basic_prey._health = 100

        result = phase.execute(basic_predator, basic_prey)
        assert result is False


class TestSimulation:
    def test_single_simulation(self):
        sim = Simulation()
        result = sim.run_single_simulation()
        assert isinstance(result, bool)

    def test_multiple_simulations(self):
        sim = Simulation()
        results = sim.run_multiple_simulations(10)

        assert results["total_simulations"] == 10
        assert results["predator_wins"] + results["prey_escapes"] == 10


class TestBasicBodyParts:
    def test_leg_properties(self):
        leg = Leg()
        assert leg.movement_contribution == 1
        assert leg.attack_power == 0

    def test_wing_properties(self):
        wing = Wing()
        assert wing.movement_contribution == 1
        assert wing.attack_power == 0

    def test_claw_properties(self):
        claw = Claw()
        assert claw.movement_contribution == 0
        assert claw.attack_power == 5

    def test_teeth_properties(self):
        teeth = Teeth()
        assert teeth.movement_contribution == 0
        assert teeth.attack_power == 4


class TestClawDecorator:
    @pytest.fixture
    def base_claw(self):
        return Claw()

    def test_small_claw_multiplication(self, base_claw):
        small_claw = ClawDecorator(base_claw, "small")
        assert small_claw.attack_power == base_claw.attack_power * 2
        assert small_claw.movement_contribution == 0

    def test_medium_claw_multiplication(self, base_claw):
        medium_claw = ClawDecorator(base_claw, "medium")
        assert medium_claw.attack_power == base_claw.attack_power * 3
        assert medium_claw.movement_contribution == 0

    def test_big_claw_multiplication(self, base_claw):
        big_claw = ClawDecorator(base_claw, "big")
        assert big_claw.attack_power == base_claw.attack_power * 4
        assert big_claw.movement_contribution == 0

    def test_invalid_size_claw(self, base_claw):
        invalid_claw = ClawDecorator(base_claw, "invalid_size")
        assert (
            invalid_claw.attack_power == base_claw.attack_power
        )  # Should default to multiplier of 1
        assert invalid_claw.movement_contribution == 0


class TestTeethDecorator:
    @pytest.fixture
    def base_teeth(self):
        return Teeth()

    def test_low_sharpness_teeth(self, base_teeth):
        low_teeth = TeethDecorator(base_teeth, "low")
        assert low_teeth.attack_power == base_teeth.attack_power + 3
        assert low_teeth.movement_contribution == 0

    def test_medium_sharpness_teeth(self, base_teeth):
        medium_teeth = TeethDecorator(base_teeth, "medium")
        assert medium_teeth.attack_power == base_teeth.attack_power + 4
        assert medium_teeth.movement_contribution == 0

    def test_high_sharpness_teeth(self, base_teeth):
        high_teeth = TeethDecorator(base_teeth, "high")
        assert high_teeth.attack_power == base_teeth.attack_power + 5
        assert high_teeth.movement_contribution == 0

    def test_invalid_sharpness_teeth(self, base_teeth):
        invalid_teeth = TeethDecorator(base_teeth, "invalid_sharpness")
        assert (
            invalid_teeth.attack_power == base_teeth.attack_power
        )  # Should default to no boost
        assert invalid_teeth.movement_contribution == 0


if __name__ == "__main__":
    pytest.main([__file__])
