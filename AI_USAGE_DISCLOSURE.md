# AI Usage Disclosure

## Toucan Payments Engineering Challenge 2026

---

## 1. Which AI Tools Were Used

- **Claude Code** (Anthropic) — Used as an AI coding assistant for generating tests.

---

## 2. What I Used AI For

1. **Test Generation** — Asked AI to generate the integration tests for the transaction API.

---

## 3. What AI Generated or Suggested

| Component | What AI Suggested |
|---|---|
| Integration Tests | 8 integration tests using MockMvc |

---

## 4. What I Changed, Corrected, or Rejected

| AI Suggestion | My Change | Reason |
|---|---|---|
| Test with `andDo(print())` debug output | Removed debug output | Cleaner test output |
| Tests with some missing test cases | Added duplicate ID rejection test | Required by docx Section 6C |

---

## 5. What AI Got Wrong (That I Had to Fix)

| Issue | How I Fixed It |
|---|---|
| Initially only 6 tests | Added missing validation test and duplicate ID test | Required by docx Section 6 |
| IDE diagnostics showed import errors | Fixed package structure by moving files to proper model/repository/service/controller/dto/exception packages | Proper Spring Boot project structure |

---

## 6. How I Checked the Final Result Works

1. **Ran all tests** — `.\mvnw.cmd clean test` — All 8 tests pass
2. **Manual API testing** — Used Postman to test all endpoints
3. **Error testing** — Tested 400 (validation), 404 (not found), 409 (duplicate ID, invalid transition) scenarios

---
