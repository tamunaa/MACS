import abc

from app.weather_data import Observer


class Subject(abc.ABC):
    @abc.abstractmethod
    def register_observer(self, observer: Observer) -> None:
        pass

    @abc.abstractmethod
    def remove_observer(self, observer: Observer) -> None:
        pass

    @abc.abstractmethod
    def notify_observers(self) -> None:
        pass

