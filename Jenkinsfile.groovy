pipeline {
	agent {
		docker {
			image 'python:3.7.2'
			args '-u root'
		}
	}
	stages {
		stage('Set up') {
			steps {
				script {
					sh 'rm -rf  capstone-project'
				}
			}
		}
		stage('SCM Checkout') {
			steps {
				script {
					sh 'git clone https://github.com/mvakkasoglu/capstone-project.git'
				}
			}
		}
		stage('Build') {
			steps {
				script {
				    dir('./capstone-project') {
				        sh 'pip install -r requirements.txt'
				    }
			    }
			}
		}
		stage('Test') {
			steps {
				script {
                    sh "echo 'Test'"
				}
			}	
		}
		stage('Publish') {
			environment{ // specifies an environement variable that will be used later in the steps https://hackernoon.com/how-to-create-a-cd-pipeline-with-kubernetes-ansible-and-jenkins-i6c03yp2
				registryCredentials = 'dockerhub'
			}
			steps {
				script { // we use the docker plugin to build the image. It uses the Dockerfile in our registry by default and adds the build number as the image tag. Later on, this will be of much importance when you need to determine which Jenkins build was the source of the currently running container. 
					def appimage = docker.build registry + ":$BUILD_NUMBER"
					docker.withRegistry('', registryCredential ) { // after the image is built successfully, we push it to Docker Hub using the build number. Additionally, we add the “latest” tag to the image (a second tag) so that we allow users to pull the image without specifying the build number, should they need to.
						appimage.push()
						appimage.push('latest')
					}
				}
			}
		}
		stage('Deploy') { // the deployment stage is where we apply our deployment and service definition files to the cluster. We invoke Ansible using the playbook that we discussed earlier. Note that we are passing the image_id as a command-line variable. This value is automatically substituted for the image name in the deployment file.
			steps {
				script{
					def image_id = registry + ":$BUILD_NUMBER"
					sh "ansible-playbook playbook.yaml --extra-vars \"image_id=${image_id}\""
				}
			}
		}
	}
}
