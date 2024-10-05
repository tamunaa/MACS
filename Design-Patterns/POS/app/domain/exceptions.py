class AppException(Exception):
    """Base exception class"""

    pass


class NotFoundError(AppException):
    """Raised when a resource is not found"""

    pass


class DuplicateError(AppException):
    """Raised when attempting to create a duplicate resource"""
    pass


class ValidationError(AppException):
    """Raised when input validation fails"""

    pass


class StateError(AppException):
    """Raised when an operation is not allowed in current state"""

    pass


class ForbiddenError(AppException):
    """Raised when an operation is not allowed"""

    def __init__(self, message: str) -> None:
        self.message = message
