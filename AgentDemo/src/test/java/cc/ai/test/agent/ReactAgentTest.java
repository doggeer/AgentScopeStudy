package cc.ai.test.agent;

import cc.ai.context.UserContext;
import cc.ai.tools.SimpleTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.ExecutionConfig;
import io.agentscope.core.model.Model;
import io.agentscope.core.plan.PlanNotebook;
import io.agentscope.core.tool.ToolExecutionContext;
import io.agentscope.core.tool.Toolkit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.Duration;


/**
 * 教程参考：https://java.agentscope.io/zh/quickstart/agent.html
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ReactAgentTest {

    /**
     * 最小化ReAct执行单元
     */
    @Test
    public void reactAgentDemoTest() {
        // 准备工具
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new SimpleTools());

        // 创建智能体
        ReActAgent jarvis = ReActAgent.builder()
                .name("Jarvis")
                .sysPrompt("你是一个名为 Jarvis 的助手")
                .model(DashScopeChatModel.builder()
                        .apiKey("sk-74f934fdaf5d4c2f9cd0b31a745e3dd6")
                        .modelName("qwen3-max")
                        .build())
                .toolkit(toolkit)
                .build();

        // 发送消息
        Msg msg = Msg.builder()
                .textContent("你好！Jarvis，现在几点了？")
                .build();

        Msg response = jarvis.call(msg).block();
        System.out.println(response.getTextContent());
    }

    /**
     * 获取阿里的model
     * @return
     */
    public Model getModel() {
        DashScopeChatModel model = DashScopeChatModel.builder()
                .apiKey("sk-74f934fdaf5d4c2f9cd0b31a745e3dd6")
                .modelName("qwen3-max")
                .build();

        return model;
    }

    /**
     * 更多控制参数
     * 最大迭代次数（默认 10）
     * 阻止并发调用（默认 true） 防止多线程同时使用相同的agent，agent是有状态的
     */
    @Test
    public void test_01() {
        DashScopeChatModel model = DashScopeChatModel.builder()
                .apiKey("sk-74f934fdaf5d4c2f9cd0b31a745e3dd6")
                .modelName("qwen3-max")
                .build();

        ReActAgent agent = ReActAgent.builder()
                .name("Assistant")
                .sysPrompt("You are a helpful assistant.")
                .model(model)
                .maxIters(10)              // 最大迭代次数（默认 10）
                .checkRunning(true)        // 阻止并发调用（默认 true）
                .build();

        // 发送消息
        Msg msg = Msg.builder()
                .textContent("你好！只能助手，现在几点了？")
                .build();

        Msg response = agent.call(msg).block();
        System.out.println(response.getTextContent());
    }

    /**
     * 超时配置
     * 工具调用超时
     * 模型调用超时
     */
    @Test
    public void test_02() {
        ExecutionConfig modelConfig = ExecutionConfig.builder()
                .timeout(Duration.ofMinutes(2))
                .maxAttempts(3)
                .build();

        ExecutionConfig toolConfig = ExecutionConfig.builder()
                .timeout(Duration.ofSeconds(30))
                .maxAttempts(1)  // 工具通常不重试
                .build();

        ReActAgent agent = ReActAgent.builder()
                .name("Assistant")
                .model(getModel())
                .modelExecutionConfig(modelConfig)
                .toolExecutionConfig(toolConfig)
                .build();

        Msg msg = Msg.builder().textContent("请你介绍一下你自己").build();

        Msg block = agent.call(msg).block();
        System.out.println(block.getTextContent());
    }


    /**
     * 工具执行上下文
     * 将隐私内容传递给工具，而非模型（模型使用api可能会外泄）
     */
    @Test
    public void test_03() {

        ToolExecutionContext context = ToolExecutionContext.builder()
                .register(new UserContext("user-123", "王超"))
                .build();


        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new SimpleTools());

        ReActAgent agent = ReActAgent.builder()
                .name("Assistant")
                .model(getModel())
                .toolkit(toolkit)
                .toolExecutionContext(context)
                .build();



        Msg msg = Msg.builder().textContent("王超的userId是多少").build();

        Msg block = agent.call(msg).block();
        System.out.println(block.getTextContent());



    }

    /**
     * 计划管理
     * 启用 PlanNotebook 支持复杂多步骤任务：
     */
    @Test
    public void test_04() {

        // 自定义配置
        PlanNotebook planNotebook = PlanNotebook.builder()
                .maxSubtasks(15)
                .build();

        ReActAgent agent = ReActAgent.builder()
                .name("Assistant")
                .model(getModel())
                .planNotebook(planNotebook)
                .build();



        Msg msg = Msg.builder().textContent("如果要查询王超的userId是多少应该怎么查询").build();

        Msg block = agent.call(msg).block();

        System.out.println(block.getTextContent());

    }





}
