To execute the script deopsrWindows.sh, we need:
	- a console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
	- to be located on the same directory than the script

At the begining of the script, we can find these three variables, than let us to set which part of our application we need to mount:
 - installsonarqube -> 'true' if you want to generate image and deploy postgres in minikube, 'false' if you don't need it
 - installjenkins -> 'true' to compile, generate image and deploy it to minikube
 
 The script starts also minikube if needed