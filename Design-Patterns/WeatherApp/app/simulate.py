import random

from app.observer_factory import ObserverRegistry, ObserverType
from app.weather_generator import CompositeWeatherGenerator
from app.weather_station import WeatherStation


class WeatherSimulation:
    def __init__(self, weeks: int = 20) -> None:
        self._weeks = weeks
        self._weather_station = WeatherStation()
        self._registry = ObserverRegistry()
        self._weather_generator = CompositeWeatherGenerator()

        display = self._registry.create_observer(ObserverType.WEATHER_DISPLAY)
        self._weather_station.register_observer(display)

    def run(self) -> None:
        for week in range(1, self._weeks + 1):
            self._run_week(week)

    def _run_week(self, week: int) -> None:
        print(f"\nWeek {week}:")

        self._maybe_add_observer(week)

        self._maybe_remove_observer(week)

        current_data = self._weather_station.current_data
        new_data = self._weather_generator.generate_weather(current_data, week)

        self._weather_station.set_measurements(
            new_data.temperature,
            new_data.humidity,
            new_data.wind_speed
        )

        print("---")

    def _maybe_add_observer(self, week: int) -> None:
        if 3 < week < 10:
            created_types = set(self._registry.get_created_types())
            available_types = [
                t for t in self._registry.get_available_types()
                if t != "WeatherDisplay" and t not in created_types
            ]

            if available_types and random.random() < 0.5:
                observer_type = random.choice(available_types)
                new_observer = self._registry.create_observer(observer_type)
                self._weather_station.register_observer(new_observer)
                print(f"Adding: {observer_type}")

    def _maybe_remove_observer(self, week: int) -> None:
        if week > 7:
            created_types = [
                t for t in self._registry.get_created_types()
                if t != "WeatherDisplay"
            ]

            if created_types and random.random() < 0.2:
                observer_type = random.choice(created_types)
                observer = self._registry.get_observer(observer_type)
                if observer:
                    self._weather_station.remove_observer(observer)
                    print(f"Removing: {observer_type}")
