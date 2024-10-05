from typing import List, Set

from app.observers import Subject
from app.weather_data import Observer, WeatherData


class WeatherStation(Subject):

    def __init__(self) -> None:
        self._observers: Set[Observer] = set()
        self._current_data = WeatherData(
            temperature=28.0,
            humidity=70.0,
            wind_speed=12.0
        )
        self._data_history: List[WeatherData] = [self._current_data]

    def register_observer(self, observer: Observer) -> None:
        self._observers.add(observer)
        observer.update(self._current_data)

    def remove_observer(self, observer: Observer) -> None:
        self._observers.discard(observer)

    def notify_observers(self) -> None:
        for observer in self._observers:
            observer.update(self._current_data)

    def set_measurements(self, temperature: float, humidity: float,
                         wind_speed: float) -> None:
        self._current_data = WeatherData(
            temperature=temperature,
            humidity=humidity,
            wind_speed=wind_speed
        )

        self._data_history.append(self._current_data)

        self.notify_observers()

    @property
    def current_data(self) -> WeatherData:
        return self._current_data

    @property
    def temperature(self) -> float:
        return self._current_data.temperature

    @property
    def humidity(self) -> float:
        return self._current_data.humidity

    @property
    def wind_speed(self) -> float:
        return self._current_data.wind_speed

    @property
    def data_history(self) -> List[WeatherData]:
        return list(self._data_history)