/** filtered and transformed by ARG-V */

/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.sosy_lab.sv_benchmarks.Verifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 同AZ优先能力测试
 *
 * @author robotLJW
 * @since 2023-3-9
 */
public class Main {
    private static int ZUUL_PORT = 8000;

    private static int GATEWAY_PORT = 8001;

    private static String REST_KEY = "servicecomb.routeRule.rest-provider";

    private static String FEIGN_KEY = "servicecomb.routeRule.feign-provider";

    private static String IP = "http://127.0.0.1:";

    private static String BOOT_BASE_PATH = "/router/boot/getMetadata?exit=false";

    private static String CLOUD_BASE_PATH = "/router/cloud/getMetadata?exit=false";

    private static String REST_BASE_PATH = "/rest";

    private static String FEIGN_BASE_PATH = "/feign";

    private static String ZUUL_REST_CLOUD_BASE_PATH = IP + ZUUL_PORT + REST_BASE_PATH + CLOUD_BASE_PATH;

    private static String ZUUL_FEIGN_BOOT_BASE_PATH = IP + ZUUL_PORT + FEIGN_BASE_PATH + BOOT_BASE_PATH;

    private static String ZUUL_FEIGN_CLOUD_BASE_PATH = IP + ZUUL_PORT + FEIGN_BASE_PATH + CLOUD_BASE_PATH;

    private static String GATEWAY_REST_CLOUD_BASE_PATH = IP + GATEWAY_PORT + REST_BASE_PATH + CLOUD_BASE_PATH;

    private static String GATEWAY_FEIGN_BOOT_BASE_PATH = IP + GATEWAY_PORT + FEIGN_BASE_PATH + BOOT_BASE_PATH;

    private static String GATEWAY_FEIGN_CLOUD_BASE_PATH = IP + GATEWAY_PORT + FEIGN_BASE_PATH + CLOUD_BASE_PATH;

    private static int TIMES = 30;

    public static List<String> SPRING_CLOUD_VERSIONS_FOR_ZUUL = Arrays
            .asList("Edgware.SR2", "Finchley.RELEASE", "Greenwich.RELEASE", "Hoxton.RELEASE");

    public static List<String> SPRING_CLOUD_VERSIONS_FOR_GATEWAY = Arrays
            .asList("Finchley.RELEASE", "Greenwich.RELEASE", "Hoxton.RELEASE", "2020.0.0", "2021.0.0", "2021.0.3");

    private String springCloudVersion = Verifier.nondetString();

    public Main() throws Exception {
        clearConfig();
    }

    /**
     * given: 标签路由测试：同标签路由场景测试（规则含有TriggerThreshold的policy）
     * when: 触发大于triggerThreshold
     * then: 执行同AZ优先策略
     */
    /** ARG-V: suitable */
	 public void testRouterWithTriggerThresholdPolicyRuleOne() throws Exception {
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testTriggerThresholdPolicyAZRule(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testTriggerThresholdPolicyAZRule(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * given：标签路由测试：同标签路由场景测试（规则含有TriggerThreshold的policy）
     * when：触发小于triggerThreshold
     * then：非同AZ优先策略
     *
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testRouterWithTriggerThresholdPolicyRuleTwo() throws Exception {
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testTriggerThresholdPolicyAZRuleTwo(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testTriggerThresholdPolicyAZRuleTwo(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：大于minAllInstances和大于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */

    /** ARG-V: suitable */
	 public void testRouterPolicyRuleOne()throws Exception{
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testPolicyAZRuleOne(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testPolicyAZRuleOne(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：大于minAllInstances和小于triggerThreshold场景
     * then：非同AZ优先策略
     *
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testRouterPolicyRuleTwo()throws Exception{
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testPolicyAZRuleTwo(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testPolicyAZRuleTwo(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：小于minAllInstances和大于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testRouterPolicyRuleThree()throws Exception{
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testPolicyAZRuleThree(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testPolicyAZRuleThree(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * given：测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * when：小于minAllInstances和小于triggerThreshold场景
     * then：同AZ优先策略
     *
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testRouterPolicyRuleFour()throws Exception{
        // 测试zuul场景：SPRING_CLOUD_VERSIONS_FOR_ZUUL中的版本，才带有zuul的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_ZUUL.contains(springCloudVersion)) {
            testPolicyAZRuleFour(ZUUL_REST_CLOUD_BASE_PATH, ZUUL_FEIGN_BOOT_BASE_PATH, ZUUL_FEIGN_CLOUD_BASE_PATH);
        }

        // 测试gateway场景：SPRING_CLOUD_VERSIONS_FOR_GATEWAY中的版本，才带有gateway的依赖
        if (SPRING_CLOUD_VERSIONS_FOR_GATEWAY.contains(springCloudVersion)) {
            testPolicyAZRuleFour(GATEWAY_REST_CLOUD_BASE_PATH, GATEWAY_FEIGN_BOOT_BASE_PATH, GATEWAY_FEIGN_CLOUD_BASE_PATH);
        }
        clearConfig();
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold的policy
     * 触发大于triggerThreshold
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testTriggerThresholdPolicyAZRule(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold的policy
     * 触发小于于triggerThreshold
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testTriggerThresholdPolicyAZRuleTwo(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);

        int restCloudAZ1 = 0, restCloudAZ2 = 0;
        int feignBootAZ1 = 0, feignBootAZ2 = 0;
        int feignCloudAZ1 = 0, feignCloudAZ2 = 0;
        for (int i = 0; i < TIMES; i++) {
            if (Verifier.nondetBoolean()) {
                restCloudAZ1++;
            } else if (Verifier.nondetBoolean()) {
                restCloudAZ2++;
            }

            if (Verifier.nondetBoolean()) {
                feignBootAZ1++;
            } else if (Verifier.nondetBoolean()) {
                feignBootAZ2++;
            }

            if (Verifier.nondetBoolean()) {
                feignCloudAZ1++;
            } else if (Verifier.nondetBoolean()) {
                feignCloudAZ2++;
            }
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 大于minAllInstances和大于triggerThreshold场景
     * 同AZ优先策略
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testPolicyAZRuleOne(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "          minAllInstances: 2\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 大于minAllInstances和小于triggerThreshold场景
     * 非同AZ优先策略
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testPolicyAZRuleTwo(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "          minAllInstances: 2\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);

        int restCloudAZ1 = 0, restCloudAZ2 = 0;
        int feignBootAZ1 = 0, feignBootAZ2 = 0;
        int feignCloudAZ1 = 0, feignCloudAZ2 = 0;
        for (int i = 0; i < TIMES; i++) {
            if (Verifier.nondetBoolean()) {
                restCloudAZ1++;
            } else if (Verifier.nondetBoolean()) {
                restCloudAZ2++;
            }

            if (Verifier.nondetBoolean()) {
                feignBootAZ1++;
            } else if (Verifier.nondetBoolean()) {
                feignBootAZ2++;
            }

            if (Verifier.nondetBoolean()) {
                feignCloudAZ1++;
            } else if (Verifier.nondetBoolean()) {
                feignCloudAZ2++;
            }
        }
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 小于minAllInstances和大于triggerThreshold场景
     * 同AZ优先策略
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testPolicyAZRuleThree(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 40\n"
                + "          minAllInstances: 10\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 测试tag匹配规则同AZ优先标签路由功能：规则含有TriggerThreshold和minAllInstances的policy
     * 小于minAllInstances和小于triggerThreshold场景
     * 同AZ优先策略
     *
     * @param restCloudBasePath
     * @param feignBootBasePath
     * @param feignCloudBasePath
     * @throws InterruptedException
     */
    /** ARG-V: suitable */
	 public void testPolicyAZRuleFour(String restCloudBasePath, String feignBootBasePath, String feignCloudBasePath) throws Exception {
        // 规则含有TriggerThreshold的policy
        String CONTENT = "---\n"
                + "- kind: routematcher.sermant.io/tag\n"
                + "  description: tag-az-rule-trigger-threshold-test\n"
                + "  rules:\n"
                + "    - precedence: 1\n"
                + "      match:\n"
                + "        tags:\n"
                + "          zone:\n"
                + "            exact: 'az1'\n"
                + "            caseInsensitive: false\n"
                + "        policy:\n"
                + "          triggerThreshold: 90\n"
                + "          minAllInstances: 10\n"
                + "      route:\n"
                + "        - tags:\n"
                + "            zone: CONSUMER_TAG\n";
        TimeUnit.SECONDS.sleep(3);
    }


    private void clearConfig() throws Exception {
        TimeUnit.SECONDS.sleep(3);
    }

	/** This main was generated by ARG-V */
	
	public static void main(String[] args) throws Exception {
		Main instance = new Main();
		instance.testRouterWithTriggerThresholdPolicyRuleOne();
		instance.testRouterWithTriggerThresholdPolicyRuleTwo();
		instance.testRouterPolicyRuleOne();
		instance.testRouterPolicyRuleTwo();
		instance.testRouterPolicyRuleThree();
		instance.testRouterPolicyRuleFour();
		instance.testTriggerThresholdPolicyAZRule(Verifier.nondetString(), Verifier.nondetString(),
				Verifier.nondetString());
		instance.testTriggerThresholdPolicyAZRuleTwo(Verifier.nondetString(), Verifier.nondetString(),
				Verifier.nondetString());
		instance.testPolicyAZRuleOne(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString());
		instance.testPolicyAZRuleTwo(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString());
		instance.testPolicyAZRuleThree(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString());
		instance.testPolicyAZRuleFour(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString());
		instance.clearConfig();
	}

}
