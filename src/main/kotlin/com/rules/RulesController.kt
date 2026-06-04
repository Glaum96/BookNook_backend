package com.rules

import com.rules.model.Rule
import com.rules.model.getRulesFromDB
import com.rules.model.updateRuleInDB
import com.users.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rules")
class RulesController {

    @Autowired
    private lateinit var userUtil: UserUtil

    @GetMapping
    fun getRules(): List<Rule> {
        return getRulesFromDB()
    }

    data class UpdateRuleRequest(val enabled: Boolean, val value: Int? = null)

    @PutMapping("/{ruleId}")
    fun updateRule(
        @PathVariable ruleId: String,
        @RequestBody body: UpdateRuleRequest,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "message" to "Forbidden"), HttpStatus.FORBIDDEN)
        }

        updateRuleInDB(ruleId, body.enabled, body.value)
        return ResponseEntity(mapOf("success" to true), HttpStatus.OK)
    }
}
