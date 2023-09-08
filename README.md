## ✅ Commit Convention

| Type | Description |
| --- | --- |
| feat | 신규 기능 구현 작업 |
| refactor | 리팩토링 작업 |
| docs | 문서화 관련 작업 |
| fix | 버그 수정 |
| style | 코드 스타일 관련 작업 |
| rename | 변수/클래스/메서드 명 변경 |
| chore | for other operations commit |
- 예시

```
[feat] 소셜 로그인 기능 구현
[refactor] memberService 리팩토링
[docs] 소셜 로그인 스웨거 설정
[fix] 만료된 토큰 요청 시 검증 안하는 문제 수정
[style] 주석 제거
[rename] "memberInfoDto" 를 "memberInfoRequest" 으로 변경
[chore] h2 의존성 추가

```

## 🧱 Branch Strategy

- Git flow
    - main
        - 배포 target
    - dev
    - feature/**
        - 예시: feature/member-api
    - hotfix/**
        - 예시: hotfix/login

## 👨‍💻 Code review

- PR 시 모두의 approve 를 받은 경우에 dev 에 merge
- ...

## ⚒️ CI/CD Architecture

- will be updated soon...