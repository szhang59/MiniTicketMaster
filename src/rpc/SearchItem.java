package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		String userId = session.getAttribute("user_id").toString();
		
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
		
//		String userId = request.getParameter("user_id");
		
//		TicketMasterAPI tmpAPI = new TicketMasterAPI();
//		List<Item> items = tmpAPI.search(lat, lon, term);
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		List<Item> items = conn.searchItems(lat, lon, term);		
		List<JSONObject> list = new ArrayList<>();
		
		Set<String> favorite = conn.getFavoriteItemIds(userId);
		try {
			for(Item item : items) {
				JSONObject obj = item.toJSONObject();
				// check if this is a favorite obj
				// required by frontend to correctly display favorite items
				obj.put("favorite", favorite.contains(item.getItemId()));
				
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray arr = new JSONArray(list);
		RpcHelper.writeJsonArray(response, arr);
				
		
		
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "user1"));
//			array.put(new JSONObject().put("username", "user2"));			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
