from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel, Field
from typing import List, Dict, Any, Callable, Optional
from motor.motor_asyncio import AsyncIOMotorClient
from bson import ObjectId
import uvicorn
import json
import datetime

app = FastAPI()

# Подключение к MongoDB
client = AsyncIOMotorClient("mongodb://localhost:27017")
db = client["job_matching"]

# Определяем тип функции совпадения
MatchFunction = Callable[[Any, Any], float]

class Match(BaseModel):
    criterion: str
    score: float
    percentage: float

class MatchInfo(BaseModel):
    score: float
    matches: List[Match]

class WorkerInformation(BaseModel):
    name: str
    birth_date: datetime.date
    city: str

class WorkerSearchCriteria(BaseModel):
    work_experience: str
    material_knowledge: str
    blueprint_reading: str
    team_management: str
    tool_experience: str
    safety_knowledge: str
    communication: str
    project_experience: str
    planning: str
    mechanics_knowledge: str
    material_experience: str
    construction_experience: str
    hvac_experience: str
    electrical_experience: str
    budget_management: str
    foundation_experience: str
    problem_solving: str
    roofing_experience: str
    project_management: str
    glazing_experience: str
    insulation_experience: str
    finishing_experience: str
    subcontractor_management: str
    exterior_finishing: str
    machinery_experience: str

class WorkerReview(BaseModel):
    company_name: str
    rating: int
    review_text: str

class Worker(BaseModel):
    username: str
    password: str
    account_type: str
    information: WorkerInformation
    search_criteria: WorkerSearchCriteria
    reviews: List[WorkerReview] = []

class EmployerInformation(BaseModel):
    company_name: str
    industry: str
    company_size: str

class EmployerSearchCriteria(BaseModel):
    work_experience: str
    material_knowledge: str
    blueprint_reading: str
    team_management: str
    tool_experience: str
    safety_knowledge: str
    communication: str
    project_experience: str
    planning: str
    mechanics_knowledge: str
    material_experience: str
    construction_experience: str
    hvac_experience: str
    electrical_experience: str
    budget_management: str
    foundation_experience: str
    problem_solving: str
    roofing_experience: str
    project_management: str
    glazing_experience: str
    insulation_experience: str
    finishing_experience: str
    subcontractor_management: str
    exterior_finishing: str
    machinery_experience: str

class EmployerReview(BaseModel):
    company_name: str
    rating: int
    review_text: str

class Employer(BaseModel):
    username: str
    password: str
    account_type: str
    information: EmployerInformation
    search_criteria: EmployerSearchCriteria
    reviews: List[EmployerReview] = []

class User(BaseModel):
    username: str
    password: str
    account_type: str

async def get_worker_by_username(username: str):
    return await db.workers.find_one({"username": username})

async def get_employer_by_username(username: str):
    return await db.employers.find_one({"username": username})

@app.post("/register/")
async def register(user: User):
    if user.account_type == "worker":
        existing_worker = await get_worker_by_username(user.username)
        if existing_worker:
            raise HTTPException(status_code=400, detail="Worker already exists")
        worker_data = {
            "username": user.username,
            "password": user.password,
            "account_type": user.account_type,
            "information": {},
            "search_criteria": {},
            "reviews": []
        }
        result = await db.workers.insert_one(worker_data)
        worker = await db.workers.find_one({"_id": result.inserted_id})
        return worker
    elif user.account_type == "employer":
        existing_employer = await get_employer_by_username(user.username)
        if existing_employer:
            raise HTTPException(status_code=400, detail="Employer already exists")
        employer_data = {
            "username": user.username,
            "password": user.password,
            "account_type": user.account_type,
            "information": {},
            "search_criteria": {},
            "reviews": []
        }
        result = await db.employers.insert_one(employer_data)
        employer = await db.employers.find_one({"_id": result.inserted_id})
        return employer
    else:
        raise HTTPException(status_code=400, detail="Invalid account type")

@app.post("/login/")
async def login(user: User):
    if user.account_type == "worker":
        worker = await get_worker_by_username(user.username)
        if not worker or worker["password"] != user.password:
            raise HTTPException(status_code=400, detail="Invalid username or password")
        return worker
    elif user.account_type == "employer":
        employer = await get_employer_by_username(user.username)
        if not employer or employer["password"] != user.password:
            raise HTTPException(status_code=400, detail="Invalid username or password")
        return employer
    else:
        raise HTTPException(status_code=400, detail="Invalid account type")

@app.post("/update_worker_info/")
async def update_worker_info(username: str, info: WorkerInformation):
    worker = await get_worker_by_username(username)
    if not worker:
        raise HTTPException(status_code=404, detail="Worker not found")
    await db.workers.update_one({"username": username}, {"$set": {"information": info.dict()}})
    updated_worker = await get_worker_by_username(username)
    return updated_worker

@app.post("/update_employer_info/")
async def update_employer_info(username: str, info: EmployerInformation):
    employer = await get_employer_by_username(username)
    if not employer:
        raise HTTPException(status_code=404, detail="Employer not found")
    await db.employers.update_one({"username": username}, {"$set": {"information": info.dict()}})
    updated_employer = await get_employer_by_username(username)
    return updated_employer

@app.get("/workers/")
async def get_workers():
    workers = await db.workers.find().to_list(length=100)
    return workers

@app.get("/employers/")
async def get_employers():
    employers = await db.employers.find().to_list(length=100)
    return employers

def match_exact(value1: Any, value2: Any) -> float:
    return 1.0 if value1 == value2 else 0.0

def match_list_overlap(list1: List[Any], list2: List[Any]) -> float:
    overlap = set(list1) & set(list2)
    return len(overlap) / len(list2) if list2 else 0.0

def match_ratio(value1: float, value2: float) -> float:
    return min(value1 / value2, 1.0)

# Обновленные критерии для работодателей и работников с весами
employer_criteria = {
    "work_experience": {
        "worker_attr": "work_experience",
        "employer_attr": "work_experience",
        "weight": 3,
        "match_func": match_ratio
    },
    "material_knowledge": {
        "worker_attr": "material_knowledge",
        "employer_attr": "material_knowledge",
        "weight": 2,
        "match_func": match_exact
    },
    "blueprint_reading": {
        "worker_attr": "blueprint_reading",
        "employer_attr": "blueprint_reading",
        "weight": 2,
        "match_func": match_exact
    },
    "team_management": {
        "worker_attr": "team_management",
        "employer_attr": "team_management",
        "weight": 2,
        "match_func": match_exact
    },
    "tool_experience": {
        "worker_attr": "tool_experience",
        "employer_attr": "tool_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "safety_knowledge": {
        "worker_attr": "safety_knowledge",
        "employer_attr": "safety_knowledge",
        "weight": 2,
        "match_func": match_exact
    },
    "communication": {
        "worker_attr": "communication",
        "employer_attr": "communication",
        "weight": 2,
        "match_func": match_exact
    },
    "project_experience": {
        "worker_attr": "project_experience",
        "employer_attr": "project_experience",
        "weight": 2,
        "match_func": match_ratio
    },
    "planning": {
        "worker_attr": "planning",
        "employer_attr": "planning",
        "weight": 2,
        "match_func": match_exact
    },
    "mechanics_knowledge": {
        "worker_attr": "mechanics_knowledge",
        "employer_attr": "mechanics_knowledge",
        "weight": 1,
        "match_func": match_exact
    },
    "material_experience": {
        "worker_attr": "material_experience",
        "employer_attr": "material_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "construction_experience": {
        "worker_attr": "construction_experience",
        "employer_attr": "construction_experience",
        "weight": 3,
        "match_func": match_exact
    },
    "hvac_experience": {
        "worker_attr": "hvac_experience",
        "employer_attr": "hvac_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "electrical_experience": {
        "worker_attr": "electrical_experience",
        "employer_attr": "electrical_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "budget_management": {
        "worker_attr": "budget_management",
        "employer_attr": "budget_management",
        "weight": 2,
        "match_func": match_exact
    },
    "foundation_experience": {
        "worker_attr": "foundation_experience",
        "employer_attr": "foundation_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "problem_solving": {
        "worker_attr": "problem_solving",
        "employer_attr": "problem_solving",
        "weight": 2,
        "match_func": match_exact
    },
    "roofing_experience": {
        "worker_attr": "roofing_experience",
        "employer_attr": "roofing_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "project_management": {
        "worker_attr": "project_management",
        "employer_attr": "project_management",
        "weight": 3,
        "match_func": match_exact
    },
    "glazing_experience": {
        "worker_attr": "glazing_experience",
        "employer_attr": "glazing_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "insulation_experience": {
        "worker_attr": "insulation_experience",
        "employer_attr": "insulation_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "finishing_experience": {
        "worker_attr": "finishing_experience",
        "employer_attr": "finishing_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "subcontractor_management": {
        "worker_attr": "subcontractor_management",
        "employer_attr": "subcontractor_management",
        "weight": 2,
        "match_func": match_exact
    },
    "exterior_finishing": {
        "worker_attr": "exterior_finishing",
        "employer_attr": "exterior_finishing",
        "weight": 1,
        "match_func": match_exact
    },
    "machinery_experience": {
        "worker_attr": "machinery_experience",
        "employer_attr": "machinery_experience",
        "weight": 1,
        "match_func": match_exact
    }
}

worker_criteria = {
    "work_experience": {
        "worker_attr": "work_experience",
        "employer_attr": "work_experience",
        "weight": 3,
        "match_func": match_ratio
    },
    "material_knowledge": {
        "worker_attr": "material_knowledge",
        "employer_attr": "material_knowledge",
        "weight": 2,
        "match_func": match_exact
    },
    "blueprint_reading": {
        "worker_attr": "blueprint_reading",
        "employer_attr": "blueprint_reading",
        "weight": 2,
        "match_func": match_exact
    },
    "team_management": {
        "worker_attr": "team_management",
        "employer_attr": "team_management",
        "weight": 2,
        "match_func": match_exact
    },
    "tool_experience": {
        "worker_attr": "tool_experience",
        "employer_attr": "tool_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "safety_knowledge": {
        "worker_attr": "safety_knowledge",
        "employer_attr": "safety_knowledge",
        "weight": 2,
        "match_func": match_exact
    },
    "communication": {
        "worker_attr": "communication",
        "employer_attr": "communication",
        "weight": 2,
        "match_func": match_exact
    },
    "project_experience": {
        "worker_attr": "project_experience",
        "employer_attr": "project_experience",
        "weight": 2,
        "match_func": match_ratio
    },
    "planning": {
        "worker_attr": "planning",
        "employer_attr": "planning",
        "weight": 2,
        "match_func": match_exact
    },
    "mechanics_knowledge": {
        "worker_attr": "mechanics_knowledge",
        "employer_attr": "mechanics_knowledge",
        "weight": 1,
        "match_func": match_exact
    },
    "material_experience": {
        "worker_attr": "material_experience",
        "employer_attr": "material_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "construction_experience": {
        "worker_attr": "construction_experience",
        "employer_attr": "construction_experience",
        "weight": 3,
        "match_func": match_exact
    },
    "hvac_experience": {
        "worker_attr": "hvac_experience",
        "employer_attr": "hvac_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "electrical_experience": {
        "worker_attr": "electrical_experience",
        "employer_attr": "electrical_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "budget_management": {
        "worker_attr": "budget_management",
        "employer_attr": "budget_management",
        "weight": 2,
        "match_func": match_exact
    },
    "foundation_experience": {
        "worker_attr": "foundation_experience",
        "employer_attr": "foundation_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "problem_solving": {
        "worker_attr": "problem_solving",
        "employer_attr": "problem_solving",
        "weight": 2,
        "match_func": match_exact
    },
    "roofing_experience": {
        "worker_attr": "roofing_experience",
        "employer_attr": "roofing_experience",
        "weight": 2,
        "match_func": match_exact
    },
    "project_management": {
        "worker_attr": "project_management",
        "employer_attr": "project_management",
        "weight": 3,
        "match_func": match_exact
    },
    "glazing_experience": {
        "worker_attr": "glazing_experience",
        "employer_attr": "glazing_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "insulation_experience": {
        "worker_attr": "insulation_experience",
        "employer_attr": "insulation_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "finishing_experience": {
        "worker_attr": "finishing_experience",
        "employer_attr": "finishing_experience",
        "weight": 1,
        "match_func": match_exact
    },
    "subcontractor_management": {
        "worker_attr": "subcontractor_management",
        "employer_attr": "subcontractor_management",
        "weight": 2,
        "match_func": match_exact
    },
    "exterior_finishing": {
        "worker_attr": "exterior_finishing",
        "employer_attr": "exterior_finishing",
        "weight": 1,
        "match_func": match_exact
    },
    "machinery_experience": {
        "worker_attr": "machinery_experience",
        "employer_attr": "machinery_experience",
        "weight": 1,
        "match_func": match_exact
    }
}

@app.post("/rank_workers/")
async def rank_workers_api(employer: Employer, workers: List[Worker]):
    ranked_workers = sorted(workers, key=lambda worker: worker.match_score(employer, employer_criteria).score, reverse=True)
    result = [{"worker": worker.dict(), "match_info": worker.match_score(employer, employer_criteria).dict()} for worker in ranked_workers]
    
    # Отладочные принты
    print(f"\n\nEmployer:\n{json.dumps(employer.dict(), indent=4)}\n\n")
    for worker in workers:
        print(f"Worker:\n{json.dumps(worker.dict(), indent=4)}\n\n")
    print(f"Ranked Workers:\n{json.dumps(result, indent=4)}\n\n")
    
    return result

@app.post("/rank_employers/")
async def rank_employers_api(worker: Worker, employers: List[Employer]):
    ranked_employers = sorted(employers, key=lambda employer: employer.match_score(worker, worker_criteria).score, reverse=True)
    result = [{"employer": employer.dict(), "match_info": employer.match_score(worker, worker_criteria).dict()} for employer in ranked_employers]
    
    # Отладочные принты
    print(f"\n\nWorker:\n{json.dumps(worker.dict(), indent=4)}\n\n")
    for employer in employers:
        print(f"Employer:\n{json.dumps(employer.dict(), indent=4)}\n\n")
    print(f"Ranked Employers:\n{json.dumps(result, indent=4)}\n\n")
    
    return result

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")
