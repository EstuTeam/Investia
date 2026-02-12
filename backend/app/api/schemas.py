"""
Standardized API Response Schemas
Provides consistent response format across all endpoints
"""
from pydantic import BaseModel, Field
from typing import Optional, Any, List, Generic, TypeVar
from datetime import datetime

T = TypeVar('T')


class ErrorDetail(BaseModel):
    """Structured error information"""
    code: str = Field(..., description="Machine-readable error code")
    message: str = Field(..., description="Human-readable error message")
    details: Optional[Any] = Field(None, description="Additional error context")


class APIResponse(BaseModel):
    """Standard API response wrapper"""
    success: bool = True
    message: Optional[str] = None
    data: Optional[Any] = None
    error: Optional[ErrorDetail] = None
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())


class PaginatedResponse(BaseModel):
    """Paginated list response"""
    success: bool = True
    data: List[Any] = []
    pagination: dict = Field(default_factory=lambda: {
        "page": 1,
        "size": 20,
        "total": 0,
        "total_pages": 0,
        "has_next": False,
        "has_prev": False,
    })

    @classmethod
    def create(cls, items: List[Any], page: int, size: int, total: int):
        """Create a paginated response"""
        total_pages = (total + size - 1) // size if size > 0 else 0
        return cls(
            data=items,
            pagination={
                "page": page,
                "size": size,
                "total": total,
                "total_pages": total_pages,
                "has_next": page < total_pages,
                "has_prev": page > 1,
            }
        )


def error_response(code: str, message: str, details: Any = None) -> dict:
    """Create a standardized error response dict"""
    return {
        "success": False,
        "error": {
            "code": code,
            "message": message,
            "details": details,
        },
        "timestamp": datetime.utcnow().isoformat(),
    }


def success_response(data: Any = None, message: str = None) -> dict:
    """Create a standardized success response dict"""
    return {
        "success": True,
        "message": message,
        "data": data,
        "timestamp": datetime.utcnow().isoformat(),
    }
