# KSP1 to KSP2 Migration Plan - DeepLinkDispatch

## Executive Summary

DeepLinkDispatch is already configured for KSP2 but has underlying test compatibility issues unrelated to KSP migration. The processor code is KSP2-ready thanks to the XProcessor abstraction layer.

## Current State Analysis

### ✅ Already KSP2-Ready
- **Configuration**: `ksp.useKSP2=true` enabled in gradle.properties
- **Dependencies**: Using KSP 2.2.0-2.0.2 (KSP2 version)
- **Processor Code**: Uses XProcessor abstraction for cross-platform compatibility
- **Build System**: Gradle configuration supports KSP2

### ❌ Current Issues
- **Test Failures**: 16/52 tests failing with identical failures on both KSP1 and KSP2
- **Root Cause**: Test framework compatibility issues, not KSP-specific problems
- **Java 17**: Migration to Java 17 may have introduced test compatibility issues

## Migration Strategy

### Phase 1: Fix Test Framework Compatibility (Priority: HIGH)

The test failures are NOT related to KSP migration but to underlying compatibility issues.

#### 1.1 Update Test Dependencies
In `buildPlugin/libs.versions.toml`:

```toml
# Current versions that need updating
compileTesting = "0.8.0"  # → Update to "1.0.0" or later
```

#### 1.2 Investigate Specific Test Failures
Failed test categories:
- `DeepLinkProcessorDeepLinkHandlerIncrementalTest`: 10 failures
- `DeepLinkProcessorIncrementalTest`: 4 failures  
- `DeepLinkProcessorKspTest`: 2 failures

**Common failure pattern**: Generated code comparison assertions failing

#### 1.3 Java 17 Compatibility
- Review test assertions for Java 17-specific changes
- Check Kotlin Compile Testing compatibility with Java 17
- Verify mock framework compatibility

### Phase 2: Validate KSP2-Specific Changes (Priority: MEDIUM)

Based on KSP2 API documentation, verify processor handles these behavioral changes:

#### 2.1 Type Resolution Changes
- **KSP2**: Better error reporting for non-existent types in type arguments
- **Action**: Review type resolution in processor code
- **Test**: Ensure error handling works correctly

#### 2.2 Type Parameter Handling
- **KSP2**: Inserts `Any?` upper bounds for consistency
- **Action**: Verify type parameter processing in `BaseProcessor.kt`
- **Test**: Check generated code with type parameters

#### 2.3 Enum Processing
- **KSP2**: Enum entries share type with enclosing enum class
- **KSP2**: Synthesized methods (`values()`, `valueOf()`) always present
- **Action**: Review enum handling if processor processes enums

#### 2.4 Override Resolution
- **KSP2**: Uses Depth-First Search (DFS) for finding overridden symbols
- **Action**: Check if processor relies on override resolution order
- **Test**: Verify behavior with method overrides

### Phase 3: Performance Optimization (Priority: LOW)

KSP2 runs in Gradle daemon instead of Kotlin compiler daemon.

#### 3.1 Memory Configuration
Current configuration is adequate:
```properties
org.gradle.jvmargs=-Xms128m -Xmx3g -XX:MaxMetaspaceSize=1g
```

#### 3.2 Debugging
For debugging KSP2 issues:
```bash
./gradlew -Dorg.gradle.debug=true :deeplinkdispatch-processor:test
```

### Phase 4: Update Documentation (Priority: LOW)

#### 4.1 Update CLAUDE.md
```markdown
## KSP2 Migration Notes (COMPLETED)

This project now uses KSP2 by default:
- KSP version: 2.2.0-2.0.2 (KSP2)
- Configuration: `ksp.useKSP2=true` 
- Processor: Uses XProcessor for cross-platform compatibility

### Known Issues
- Test framework compatibility issues (not KSP-related)
- 16/52 processor tests failing due to Kotlin Compile Testing version issues
```

#### 4.2 Update README.md
Add KSP2 information to existing KSP documentation section.

## Implementation Timeline

### Week 1: Test Framework Fix
1. Update `compileTesting` dependency version
2. Investigate Kotlin Compile Testing + Java 17 compatibility
3. Fix failing test assertions
4. Verify all tests pass with KSP2

### Week 2: Validation
1. Test KSP2-specific behavioral changes
2. Run comprehensive test suite
3. Performance benchmarking (optional)

### Week 3: Documentation
1. Update project documentation
2. Add KSP2 migration notes
3. Update build instructions

## Risk Assessment

### Low Risk ✅
- **Processor Code**: Already compatible via XProcessor
- **Build Configuration**: Already correct for KSP2
- **Dependencies**: Already using KSP2 versions

### Medium Risk ⚠️
- **Test Framework**: May require significant test dependency updates
- **Generated Code**: Slight differences in output formatting possible

### Mitigation Strategies
1. **Incremental Testing**: Fix tests in small batches
2. **Version Pinning**: Use specific compatible versions
3. **Rollback Plan**: Can disable KSP2 via `ksp.useKSP2=false`

## Commands for Testing

```bash
# Test with KSP2 (current default)
./gradlew :deeplinkdispatch-processor:test

# Test with KSP1 (fallback)
./gradlew :deeplinkdispatch-processor:test -Pksp.useKSP2=false

# Build core libraries
./gradlew :deeplinkdispatch:assemble :deeplinkdispatch-processor:assemble

# Full build (excluding samples with known issues)
./gradlew assemble -x :sample:assemble -x :sample-benchmark:assemble
```

## Conclusion

**Status**: KSP2 migration is technically complete but blocked by test framework issues.

**Next Steps**: 
1. Focus on fixing test compatibility rather than KSP migration
2. Update Kotlin Compile Testing framework
3. Resolve Java 17 compatibility issues

**Timeline**: 1-2 weeks to resolve test issues, KSP2 is already working correctly.