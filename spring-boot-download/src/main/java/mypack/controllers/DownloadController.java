package mypack.controllers;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mypack.service.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class DownloadController
{
	
	@RequestMapping(value = "/update", method = RequestMethod.GET, params={"file"})
	@ResponseBody
	public void updateFile(/*@PathVariable String id,*/ @RequestParam("file") String file, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		URL url = new URL("http://localhost:8080/"+file);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("HEAD");
		if(con.getResponseCode() == HttpURLConnection.HTTP_OK) //checks if file exists
		{
			if((request.getHeader("Content-Disposition") != null) && (request.getHeader("Content-Disposition").equals("inline; filename=" + file)))
			{
				if(request.getHeader("Last-Modified") != null)
				{
					if(request.getHeader("Downloaded") != null)
					{
						new Update().with(request).with(response).fileDownload(url.toString());
					}
					else
					{
						response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
						System.out.println("downloaded error");
			            return;
					}
				}
				else
				{
					response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
					System.out.println("last modified error");
		            return;
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				System.out.println("content disposition error");
	            return;
			}
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			System.out.println("file not found");
            return;
		}
	}
}
