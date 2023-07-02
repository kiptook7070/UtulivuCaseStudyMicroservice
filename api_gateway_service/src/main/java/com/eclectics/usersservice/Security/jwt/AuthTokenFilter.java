package com.eclectics.usersservice.Security.jwt;

import com.eclectics.usersservice.Users.Users;
import com.eclectics.usersservice.Users.UsersRepository;
import com.eclectics.usersservice.utils.HttpInterceptor.UserDetailsRequestContext;
import com.eclectics.usersservice.utils.HttpInterceptor.UserRequestContext;
import com.eclectics.usersservice.Security.services.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
	@Value("${spring.application.logs.user}")
	private String userLogs;

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private Clientinformation clientinformation;
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			getLogs(request);
			String accessToken = request.getHeader("accessToken");
			String currentUserDetails = request.getHeader("userName");
			UserRequestContext.setCurrentUser(request.getHeader("userName"));
			if (request.getRequestURI().matches("/auth/signin") || request.getRequestURI().matches("/auth/signup") || request.getRequestURI().matches("/swagger-ui/") ){
				UserRequestContext.setCurrentUser("Guest");
			}
			UserDetailsRequestContext.setCurrentUserDetails(currentUserDetails);
			String jwt = accessToken;
			clientinformation.getClientInformation(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);
				//SET USER contact
				Optional<Users> user = usersRepository.findByUsername(username);
				UserRequestContext.setCurrentUser(username);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	public void getLogs(HttpServletRequest request) throws IOException {
		String currentUserDetails = request.getHeader("userName");
		Path path = Paths.get(userLogs);
		String method = request.getMethod();
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		String protocol = request.getProtocol();
		String remoteAddr = request.getRemoteAddr();
		int remotePort = request.getRemotePort();
		String userAgent = request.getHeader("User-Agent");
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String fileName = now.format(formatter)+currentUserDetails+"RequestLogs.log";

		try {
			if (Files.exists(path)) {
				// if the file exists, append the data to the end of the file
				FileWriter writer = new FileWriter(fileName);
				writer.write("method: "+ method+" uri: "+uri+" queryString: "+queryString+" protocol: "+protocol+" remoteAddr: "+remoteAddr+" "+" remotePort: "+remotePort+" userAgent: "+userAgent);
				writer.close();
				writer.close();
			} else {
				// if the file does not exist, create a new one and add the data
				FileWriter writer = new FileWriter(fileName);
				writer.write("method: "+ method+" uri: "+uri+" queryString: "+queryString+" protocol: "+protocol+" remoteAddr: "+remoteAddr+" "+" remotePort: "+remotePort+" userAgent: "+userAgent);
				writer.close();
				writer.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
