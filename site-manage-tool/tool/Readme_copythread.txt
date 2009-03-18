Copying entities can occur at multiple places in Worksite Setup and Site Info tool. 

When the copy entity collection size gets big, the copy process could take a long time. Hence the copy process should be running in a separate thread, while the user is redirected 
back to the default view, with the iFrame blocked to prevent tool interactions in the meanwhile.

SiteCopyThread is called when user does any of the following operations:
	1. duplicate a site (in Site Info) 
	2. create a new site based on a template(in Worksite Setup). 

EntityCopyThread is called when user does any of the following operations:
	1. import from site (in Site Info) 
	2. create a new site with imported content from previous sites (in Worksite Setup)