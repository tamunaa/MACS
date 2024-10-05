import uuid
from contextlib import contextmanager
from sqlite3 import Connection, Row
from typing import (
    Any,
    Callable,
    Dict,
    Generator,
    Generic,
    List,
    Optional,
    TypeVar,
)

T = TypeVar("T")


class BaseRepository(Generic[T]):
    def __init__(self, connection_factory: Callable[[], Connection]) -> None:
        self._connection_factory = connection_factory
        with self.get_connection() as conn:
            conn.row_factory = Row

    @contextmanager
    def get_connection(self) -> Generator[Connection, None, None]:
        conn = self._connection_factory()
        try:
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            conn.close()

    @contextmanager
    def transaction(self) -> Generator[None, None, None]:
        with self.get_connection() as conn:
            try:
                yield
                conn.commit()
            except Exception:
                conn.rollback()
                raise

    def generate_id(self) -> str:
        return str(uuid.uuid4())

    def execute_query(self, query: str, params: tuple[Any, ...] = ()) -> Optional[int]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(query, params)
            return cursor.lastrowid

    def execute_many(self, query: str, params_list: List[tuple[Any, ...]]) -> None:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.executemany(query, params_list)

    def exists(self, query: str, params: tuple[Any, ...] = ()) -> bool:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(query, params)
            return cursor.fetchone() is not None

    def fetch_one(
        self, query: str, params: tuple[Any, ...] = ()
    ) -> Optional[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(query, params)
            row = cursor.fetchone()
            return dict(row) if row else None

    def fetch_all(
        self, query: str, params: tuple[Any, ...] = ()
    ) -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(query, params)
            return [dict(row) for row in cursor.fetchall()]

    def count(self, query: str, params: tuple[Any, ...] = ()) -> int:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(query, params)
            result = cursor.fetchone()
            return int(result[0]) if result else 0
