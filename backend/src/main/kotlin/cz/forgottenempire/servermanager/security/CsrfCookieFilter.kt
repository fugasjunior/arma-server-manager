package cz.forgottenempire.servermanager.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Forces Spring Security's deferred CSRF token to be loaded and the XSRF-TOKEN cookie to be
 * written on every response. Without this, CookieCsrfTokenRepository only writes the cookie
 * when the token attribute is actually read, which doesn't happen on safe (GET) requests.
 */
class CsrfCookieFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val csrfToken = request.getAttribute(CsrfToken::class.java.name)
        if (csrfToken is CsrfToken) {
            csrfToken.token // triggers cookie write via CookieCsrfTokenRepository
        }
        filterChain.doFilter(request, response)
    }
}
