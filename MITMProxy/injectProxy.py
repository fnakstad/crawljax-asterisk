import re
import sys
from libmproxy.protocol.http import decoded
from bs4 import BeautifulSoup

js_file = open("scriptToInject.js", "r")
js = js_file.read()#.replace('\n', ' ')
js_file.close()

def response(context, flow):
	with decoded(flow.response):  # automatically decode gzipped responses.
		for header, value in flow.response.headers.items():
			if header.lower() == 'content-type':
				if re.match("text/html", value) is not None:
					print "Modifying HTML content"
					soup = BeautifulSoup(flow.response.content)	  		
							
					if soup.html.head is not None:
						script_tag = soup.new_tag("script", type="text/javascript")
						script_tag.append(js);
						soup.html.head.insert(0, script_tag)
						flow.response.content = str(soup)
						
						if soup.html.body is not None:
							warning_tag = soup.new_tag("div", style="background: darkblue; color: white;")
							warning_tag.append("Script injected.")
							soup.html.body.insert(0, warning_tag)
							print str(soup)
							flow.response.content = str(soup)
					