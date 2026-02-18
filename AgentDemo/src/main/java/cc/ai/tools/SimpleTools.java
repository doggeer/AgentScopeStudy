package cc.ai.tools;

import cc.ai.context.UserContext;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

public class SimpleTools {
    @Tool(name = "get_time", description = "获取当前时间")
    public String getTime(
            @ToolParam(name = "zone", description = "时区，例如：北京") String zone) {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 工具中自动注入
    @Tool(name = "query", description = "查询数据")
    public String query(
            @ToolParam(name = "sql") String sql,
            UserContext ctx  // 自动注入，无需 @ToolParam
    ) {
        return "用户 " + ctx.getUserId() + " 的查询结果";
    }
}
