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
					sh 'rm -rf  2020_03_DO_Boston_casestudy_part_1'
				}
			}
		}
		stage('SCM Checkout') {
			steps {
				script {
					sh 'git clone https://github.com/mvakkasoglu/2020_03_DO_Boston_casestudy_part_1.git'
				}
			}
		}
		stage('Build') {
			steps {
				script {
				    dir('./2020_03_DO_Boston_casestudy_part_1') {
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
			environment{
				registryCredentials = 'dockerhub'
			}
			steps {
				script {
					def appimage = docker.build registry + ":$BUILD_NUMBER"
					docker.withRegistry('', registryCredential ) {
						appimage.push()
						appimage.push('latest')
					}
				}
			}
		}
		stage('Deploy') {
			steps {
				script{
					def image_id = registry + ":$BUILD_NUMBER"
					sh "ansible-playbook playbook.yaml --extra-vars \"image_id=${image_id}\""
				}
			}
		}
	}
}
