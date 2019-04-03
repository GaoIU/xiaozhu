package com.suizhu.common.constant;

/**
 * Http状态码
 * 
 * @author gaochao
 * @date Feb 18, 2019
 */
public class HttpConstant {

	/**
	 * 100 <br>
	 * 继续发送请求。这个临时响应是用来通知客户端它的部分请求已经被服务器接收，且仍未被拒绝。客户端应当继续发送请求的剩余部分，或者如果请求已经完成，忽略这个响应。服务器必须在请求完成后向客户端发送一个最终响应。
	 */
	public final static int CONTINUE = 100;

	/**
	 * 101 <br>
	 * 服务器已经理解了客户端的请求，并将通过Upgrade
	 * 消息头通知客户端采用不同的协议来完成这个请求。在发送完这个响应最后的空行后，服务器将会切换到在Upgrade 消息头中定义的那些协议。 <br>
	 * 只有在切换新的协议更有好处的时候才应该采取类似措施。
	 */
	public final static int SWITCHING_PROTOCOLS = 101;

	/**
	 * 102 <br>
	 * 由WebDAV（RFC 2518）扩展的状态码，代表处理将被继续执行。
	 */
	public final static int PROCESSING = 102;

	/**
	 * 103 <br>
	 * IETF公布的新HTTP状态码，在头部信息到达之前，用户可以开始预加载CSS和JavaScript文件。 <br>
	 * 在头部信息到达之前，用户可以开始预加载CSS和JavaScript文件，这是一个很好的优化。 <br>
	 * 因此，向不合格的客户机发送多个标头存在各种各样的安全风险:“因此，如果客户不知道正确处理信息响应，服务器可能会避免发送HTTP / 1.1的早期提示。”
	 */
	public final static int CHECKPOINT = 103;

	/**
	 * 200 <br>
	 * 请求已成功，请求所希望的响应头或数据体将随此响应返回。
	 */
	public final static int OK = 200;

	/**
	 * 201 <br>
	 * 请求已经被实现，而且有一个新的资源已经依据请求的需要而建立，且其 URI 已经随Location 头信息返回。 <br>
	 * 假如需要的资源无法及时建立的话，应当返回 '202 Accepted'。
	 */
	public final static int CREATED = 201;

	/**
	 * 202 <br>
	 * 服务器已接受请求，但尚未处理。正如它可能被拒绝一样，最终该请求可能会也可能不会被执行。在异步操作的场合下，没有比发送这个状态码更方便的做法了。
	 */
	public final static int ACCEPTED = 202;

	/**
	 * 203 <br>
	 * 服务器已成功处理了请求，但返回的实体头部元信息不是在原始服务器上有效的确定集合，而是来自本地或者第三方的拷贝。当前的信息可能是原始版本的子集或者超集。
	 * <br>
	 * 例如，包含资源的元数据可能导致原始服务器知道元信息的超级。使用此状态码不是必须的，而且只有在响应不使用此状态码便会返回200 OK的情况下才是合适的。
	 */
	public final static int NON_AUTHORITATIVE_INFORMATION = 203;

	/**
	 * 204 <br>
	 * 服务器成功处理了请求，但不需要返回任何实体内容，并且希望返回更新了的元信息。响应可能通过实体头部的形式，返回新的或更新后的元信息。如果存在这些头部信息，则应当与所请求的变量相呼应。
	 * <br>
	 * 如果客户端是浏览器的话，那么用户浏览器应保留发送了该请求的页面，而不产生任何文档视图上的变化，即使按照规范新的或更新后的元信息应当被应用到用户浏览器活动视图中的文档。
	 * <br>
	 * 由于204响应被禁止包含任何消息体，因此它始终以消息头后的第一个空行结尾。
	 */
	public final static int NO_CONTENT = 204;

	/**
	 * 205 <br>
	 * 服务器成功处理了请求，且没有返回任何内容。但是与204响应不同，返回此状态码的响应要求请求者重置文档视图。 <br>
	 * 该响应主要是被用于接受用户输入后，立即重置表单，以便用户能够轻松地开始另一次输入。 <br>
	 * 与204响应一样，该响应也被禁止包含任何消息体，且以消息头后的第一个空行结束。
	 */
	public final static int RESET_CONTENT = 205;

	/**
	 * 206 <br>
	 * 服务器已经成功处理了部分 GET 请求。类似于 FlashGet 或者迅雷这类的 HTTP
	 * 下载工具都是使用此类响应实现断点续传或者将一个大文档分解为多个下载段同时下载。 <br>
	 * 该请求必须包含 Range 头信息来指示客户端希望得到的内容范围，并且可能包含 If-Range 来作为请求条件。 <br>
	 * 任何不支持 Range 以及 Content-Range 头的缓存都禁止缓存206响应返回的内容。
	 */
	public final static int PARTIAL_CONTENT = 206;

	/**
	 * 207 <br>
	 * 由WebDAV(RFC 2518)扩展的状态码，代表之后的消息体将是一个XML消息，并且可能依照之前子请求数量的不同，包含一系列独立的响应代码。
	 */
	public final static int MULTI_STATUS = 207;

	/**
	 * 208 <br>
	 * DAV绑定的成员已经在（多状态）响应之前的部分被列举，且未被再次包含。
	 */
	public final static int ALREADY_REPORTED = 208;

	/**
	 * 226 <br>
	 * IM Used
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc3229#section-10.4.1">Delta
	 *      encoding in HTTP</a>
	 */
	public final static int IM_USED = 226;

	/**
	 * 300 <br>
	 * 被请求的资源有一系列可供选择的回馈信息，每个都有自己特定的地址和浏览器驱动的商议信息。用户或浏览器能够自行选择一个首选的地址进行重定向。 <br>
	 * 除非这是一个 HEAD 请求，否则该响应应当包括一个资源特性及地址的列表的实体，以便用户或浏览器从中选择最合适的重定向地址。 <br>
	 * 这个实体的格式由 Content-Type 定义的格式所决定。浏览器可能根据响应的格式以及浏览器自身能力，自动作出最合适的选择。 <br>
	 * 当然，RFC 2616规范并没有规定这样的自动选择该如何进行。
	 */
	public final static int MULTIPLE_CHOICES = 300;

	/**
	 * 301 <br>
	 * 被请求的资源已永久移动到新位置，并且将来任何对此资源的引用都应该使用本响应返回的若干个 URI 之一。 <br>
	 * 如果可能，拥有链接编辑功能的客户端应当自动把请求的地址修改为从服务器反馈回来的地址。除非额外指定，否则这个响应也是可缓存的。
	 */
	public final static int MOVED_PERMANENTLY = 301;

	/**
	 * 302 <br>
	 * 请求的资源现在临时从不同的 URI 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。 <br>
	 * 只有在Cache-Control或Expires中进行了指定的情况下，这个响应才是可缓存的。
	 */
	public final static int FOUND = 302;

	/**
	 * 303 <br>
	 * 对应当前请求的响应可以在另一个 URI 上被找到，而且客户端应当采用 GET 的方式访问那个资源。 <br>
	 * 这个方法的存在主要是为了允许由脚本激活的POST请求输出重定向到一个新的资源。 <br>
	 * 这个新的 URI 不是原始资源的替代引用。同时，303响应禁止被缓存。 <br>
	 * 当然，第二个请求（重定向）可能被缓存。
	 */
	public final static int SEE_OTHER = 303;

	/**
	 * 304 <br>
	 * 如果客户端发送了一个带条件的 GET 请求且该请求已被允许，而文档的内容（自上次访问以来或者根据请求的条件）并没有改变，则服务器应当返回这个状态码。
	 * <br>
	 * 304响应禁止包含消息体，因此始终以消息头后的第一个空行结尾。
	 */
	public final static int NOT_MODIFIED = 304;

	/**
	 * 307 <br>
	 * 请求的资源现在临时从不同的URI 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。 <br>
	 * 只有在Cache-Control或Expires中进行了指定的情况下，这个响应才是可缓存的。
	 */
	public final static int TEMPORARY_REDIRECT = 307;

	/**
	 * 308 <br>
	 * 永久重定向，请求和所有将来的请求应该使用另一个URI重复。 307和308重复302和301的行为，但不允许HTTP方法更改。 <br>
	 * 例如，将表单提交给永久重定向的资源可能会顺利进行。
	 */
	public final static int PERMANENT_REDIRECT = 308;

	/**
	 * 400 <br>
	 * 1.语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。 <br>
	 * 2.请求参数有误。
	 */
	public final static int BAD_REQUEST = 400;

	/**
	 * 401 <br>
	 * 当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate 信息头用以询问用户信息。客户端可以重复提交一个包含恰当的
	 * Authorization 头信息的请求。 <br>
	 * 如果当前请求已经包含了 Authorization 证书，那么401响应代表着服务器验证已经拒绝了那些证书。
	 */
	public final static int UNAUTHORIZED = 401;

	/**
	 * 402 <br>
	 * 该状态码是为了将来可能的需求而预留的。
	 */
	public final static int PAYMENT_REQUIRED = 402;

	/**
	 * 403 <br>
	 * 服务器已经理解请求，但是拒绝执行它。与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。
	 */
	public final static int FORBIDDEN = 403;

	/**
	 * 404 <br>
	 * 请求失败，请求所希望得到的资源未被在服务器上发现。没有信息能够告诉用户这个状况到底是暂时的还是永久的。
	 */
	public final static int NOT_FOUND = 404;

	/**
	 * 405 <br>
	 * 请求行中指定的请求方法不能被用于请求相应的资源。该响应必须返回一个Allow 头信息用以表示出当前资源能够接受的请求方法的列表。 <br>
	 * 鉴于 PUT，DELETE
	 * 方法会对服务器上的资源进行写操作，因而绝大部分的网页服务器都不支持或者在默认配置下不允许上述请求方法，对于此类请求均会返回405错误。
	 */
	public final static int METHOD_NOT_ALLOWED = 405;

	/**
	 * 406 <br>
	 * 请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。 <br>
	 * 除非这是一个 HEAD 请求，否则该响应就应当返回一个包含可以让用户或者浏览器从中选择最合适的实体特性以及地址列表的实体。实体的格式由
	 * Content-Type 头中定义的媒体类型决定。 <br>
	 * 浏览器可以根据格式及自身能力自行作出最佳选择。但是，规范中并没有定义任何作出此类自动选择的标准。
	 */
	public final static int NOT_ACCEPTABLE = 406;

	/**
	 * 407 <br>
	 * 与401响应类似，只不过客户端必须在代理服务器上进行身份验证。代理服务器必须返回一个 Proxy-Authenticate
	 * 用以进行身份询问。客户端可以返回一个 Proxy-Authorization 信息头用以验证。
	 */
	public final static int PROXY_AUTHENTICATION_REQUIRED = 407;

	/**
	 * 408 <br>
	 * 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。客户端可以随时再次提交这一请求而无需进行任何更改。
	 */
	public final static int REQUEST_TIMEOUT = 408;

	/**
	 * 409 <br>
	 * 由于和被请求的资源的当前状态之间存在冲突，请求无法完成。
	 */
	public final static int CONFLICT = 409;

	/**
	 * 410 <br>
	 * 被请求的资源在服务器上已经不再可用，而且没有任何已知的转发地址。这样的状况应当被认为是永久性的。如果可能，拥有链接编辑功能的客户端应当在获得用户许可后删除所有指向这个地址的引用。
	 * <br>
	 * 如果服务器不知道或者无法确定这个状况是否是永久的，那么就应该使用404状态码。除非额外说明，否则这个响应是可缓存的。
	 */
	public final static int GONE = 410;

	/**
	 * 411 <br>
	 * 服务器拒绝在没有定义 Content-Length 头的情况下接受请求。在添加了表明请求消息体长度的有效 Content-Length
	 * 头之后，客户端可以再次提交该请求。
	 */
	public final static int LENGTH_REQUIRED = 411;

	/**
	 * 412 <br>
	 * 服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个。这个状态码允许客户端在获取资源时在请求的元信息（请求头字段数据）中设置先决条件，以此避免该请求方法被应用到其希望的内容以外的资源上。
	 */
	public final static int PRECONDITION_FAILED = 412;

	/**
	 * 413 <br>
	 * 服务器拒绝处理当前请求，因为该请求提交的实体数据大小超过了服务器愿意或者能够处理的范围。此种情况下，服务器可以关闭连接以免客户端继续发送此请求。 <br>
	 * 如果这个状况是临时的，服务器应当返回一个 Retry-After 的响应头，以告知客户端可以在多少时间以后重新尝试。
	 */
	public final static int PAYLOAD_TOO_LARGE = 413;

	/**
	 * 414 <br>
	 * 请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务。
	 */
	public final static int URI_TOO_LONG = 414;

	/**
	 * 415 <br>
	 * 对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
	 */
	public final static int UNSUPPORTED_MEDIA_TYPE = 415;

	/**
	 * 416 <br>
	 * 如果请求中包含了 Range 请求头，并且 Range 中指定的任何数据范围都与当前资源的可用范围不重合，同时请求中又没有定义 If-Range
	 * 请求头，那么服务器就应当返回416状态码。
	 */
	public final static int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

	/**
	 * 417 <br>
	 * 在请求头 Expect 中指定的预期内容无法被服务器满足，或者这个服务器是一个代理服务器，它有明显的证据证明在当前路由的下一个节点上，Expect
	 * 的内容无法被满足。
	 */
	public final static int EXPECTATION_FAILED = 417;

	/**
	 * 418 <br>
	 * I'm a teapot <br>
	 * 
	 * @see <a href=
	 *      "http://tools.ietf.org/html/rfc2324#section-2.3.2">HTCPCP/1.0</a>
	 */
	public final static int I_AM_A_TEAPOT = 418;

	/**
	 * 422 <br>
	 * 从当前客户端所在的IP地址到服务器的连接数超过了服务器许可的最大范围。通常，这里的IP地址指的是从服务器上看到的客户端地址（比如用户的网关或者代理服务器地址）。在这种情况下，连接数的计算可能涉及到不止一个终端用户。
	 */
	public final static int UNPROCESSABLE_ENTITY = 422;

	/**
	 * 423 <br>
	 * 请求格式正确，但是由于含有语义错误，无法响应。
	 */
	public final static int LOCKED = 423;

	/**
	 * 424 <br>
	 * 由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。
	 */
	public final static int FAILED_DEPENDENCY = 424;

	/**
	 * 426 <br>
	 * 客户端应当切换到TLS/1.0。
	 */
	public final static int UPGRADE_REQUIRED = 426;

	/**
	 * 428 <br>
	 * Precondition Required
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-3">Additional HTTP
	 *      Status Codes</a>
	 */
	public final static int PRECONDITION_REQUIRED = 428;

	/**
	 * 429 <br>
	 * Too Many Requests
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-4">Additional HTTP
	 *      Status Codes</a>
	 */
	public final static int TOO_MANY_REQUESTS = 429;

	/**
	 * 431 <br>
	 * Request Header Fields Too Large
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-5">Additional HTTP
	 *      Status Codes</a>
	 */
	public final static int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;

	/**
	 * 451 <br>
	 * Unavailable For Legal Reasons
	 * 
	 * @see <a href=
	 *      "https://tools.ietf.org/html/draft-ietf-httpbis-legally-restricted-status-04">An
	 *      HTTP Status Code to Report Legal Obstacles</a>
	 */
	public final static int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

	/**
	 * 500 <br>
	 * 服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。一般来说，这个问题都会在服务器的程序码出错时出现。
	 */
	public final static int INTERNAL_SERVER_ERROR = 500;

	/**
	 * 501 <br>
	 * 服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。
	 */
	public final static int NOT_IMPLEMENTED = 501;

	/**
	 * 502 <br>
	 * 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
	 */
	public final static int BAD_GATEWAY = 502;

	/**
	 * 503 <br>
	 * 由于临时的服务器维护或者过载，服务器当前无法处理请求。这个状况是临时的，并且将在一段时间以后恢复。如果能够预计延迟时间，那么响应中可以包含一个
	 * Retry-After 头用以标明这个延迟时间。 <br>
	 * 如果没有给出这个 Retry-After 信息，那么客户端应当以处理500响应的方式处理它。 <br>
	 * 注意：503状态码的存在并不意味着服务器在过载的时候必须使用它。某些服务器只不过是希望拒绝客户端的连接。
	 */
	public final static int SERVICE_UNAVAILABLE = 503;

	/**
	 * 504 <br>
	 * 作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS）收到响应。
	 * <br>
	 * 注意：某些代理服务器在DNS查询超时时会返回400或者500错误。
	 */
	public final static int GATEWAY_TIMEOUT = 504;

	/**
	 * 505 <br>
	 * 服务器不支持，或者拒绝支持在请求中使用的 HTTP
	 * 版本。这暗示着服务器不能或不愿使用与客户端相同的版本。响应中应当包含一个描述了为何版本不被支持以及服务器支持哪些协议的实体。
	 */
	public final static int HTTP_VERSION_NOT_SUPPORTED = 505;

	/**
	 * 506 <br>
	 * 由《透明内容协商协议》（RFC
	 * 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，因此在一个协商处理中不是一个合适的重点。
	 */
	public final static int VARIANT_ALSO_NEGOTIATES = 506;

	/**
	 * 507 <br>
	 * 服务器无法存储完成请求所必须的内容。这个状况被认为是临时的。WebDAV (RFC 4918)
	 */
	public final static int INSUFFICIENT_STORAGE = 507;

	/**
	 * 508 <br>
	 * Loop Detected
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.2">WebDAV Binding
	 *      Extensions</a>
	 */
	public final static int LOOP_DETECTED = 508;

	/**
	 * 509 <br>
	 * 服务器达到带宽限制。这不是一个官方的状态码，但是仍被广泛使用。
	 */
	public final static int BANDWIDTH_LIMIT_EXCEEDED = 509;

	/**
	 * 510 <br>
	 * 获取资源所需要的策略并没有没满足。（RFC 2774）
	 */
	public final static int NOT_EXTENDED = 510;

	/**
	 * 511 <br>
	 * Network Authentication Required
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-6">Additional HTTP
	 *      Status Codes</a>
	 */
	public final static int NETWORK_AUTHENTICATION_REQUIRED = 511;

	private HttpConstant() {
		super();
	}

}
