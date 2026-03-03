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

    @PutMapping("/{ruleId}")
    fun updateRule(
        @PathVariable ruleId: String,
        @RequestBody body: Map<String, Boolean>,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<Map<String, Any>> {
        if (!userUtil.validateAdminAction(authorizationHeader)) {
            return ResponseEntity(mapOf("success" to false, "message" to "Forbidden"), HttpStatus.FORBIDDEN)
        }

        val enabled = body["enabled"] ?: return ResponseEntity(
            mapOf("success" to false, "message" to "Missing 'enabled' field"),
            HttpStatus.BAD_REQUEST
        )

        updateRuleInDB(ruleId, enabled)
        return ResponseEntity(mapOf("success" to true), HttpStatus.OK)
    }
}
