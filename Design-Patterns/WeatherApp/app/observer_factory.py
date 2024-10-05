from enum import Enum
from typing import Callable, Dict, List, Optional

from app.alerts.humidity_alert import HumidityAlert
from app.alerts.temperature_alert import TemperatureAlert
from app.alerts.wind_speed_alert import WindSpeedAlert
from app.weather_data import Observer
from app.weather_display import WeatherDisplay


class ObserverType(Enum):
    WEATHER_DISPLAY = "WeatherDisplay"
    TEMPERATURE_ALERT = "TemperatureAlert"
    WIND_SPEED_ALERT = "WindSpeedAlert"
    HUMIDITY_ALERT = "HumidityAlert"

class ObserverRegistry:

    def __init__(self) -> None:
        self._factories: Dict[ObserverType, Callable[[], "Observer"]] = {
            ObserverType.WEATHER_DISPLAY: lambda: WeatherDisplay(),
            ObserverType.TEMPERATURE_ALERT: lambda: TemperatureAlert(),
            ObserverType.WIND_SPEED_ALERT: lambda: WindSpeedAlert(),
            ObserverType.HUMIDITY_ALERT: lambda: HumidityAlert()
        }
        self._created_observers: Dict[ObserverType, Observer] = {}

    def create_observer(self, observer_type: ObserverType) -> Observer:
        if observer_type not in self._factories:
            raise ValueError(f"Unknown observer type: {observer_type}")

        observer = self._factories[observer_type]()
        self._created_observers[observer_type] = observer
        return observer

    def get_observer(self, observer_type: ObserverType) -> Optional[Observer]:
        return self._created_observers.get(observer_type)

    def get_available_types(self) -> List[ObserverType]:
        return list(self._factories.keys())

    def get_created_types(self) -> List[ObserverType]:
        return list(self._created_observers.keys())

