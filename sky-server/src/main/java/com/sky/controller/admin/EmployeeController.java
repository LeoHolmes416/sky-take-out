package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")  //描述接口名称
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;  //jwt配置类

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId()); //claims放入员工id
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);  //调用配置类，配置token令牌，包括管理员密钥，管理员jwt有效时间，claims(jwt第二部分payload中负载的内容)

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()  //员工登录后返回的数据体(回显)构建
                .id(employee.getId())  //此处的id只是回显的
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)  //token中的id是伴随着整个用户线程的
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value ="员工登出")  //描述方法
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * 输入json格式的数据，返回成功信息
     */
    @PostMapping
    @ApiOperation(value ="新增员工")  //描述方法
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工: {}",employeeDTO);
        System.out.println("当前线程id: " + Thread.currentThread().getId());
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){  //
        log.info("员工分页查询，输入值为: {}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改员工登陆状态
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result startOrStop(@PathVariable("status") Integer status, long id){  //使用路径参数
        log.info("启用或禁用员工号,0禁用1启用: {},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

}
