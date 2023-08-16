pipeline {
    agent any

    environment {
        GITHUB_DOMAIN = "github.com/k8s12-CacaoEnt/"
        ROOT_REPO = "profilehub.git"
        COMM_REPO = "profile-box-comm.git"
        API_USER_REPO = "profile-box-api-user.git"
        YAML_REPO = "argocd-yaml.git"
        DOCKER_IMAGE = "hyewone/profile-box-api-user"
        DOCKER_IMAGE_TAG = "v${env.BUILD_ID}"
    }

    stages {

        stage('Checkout') {
            steps {
                    dir('app-repo') {
                        git branch: 'main', url: 'http://' + GITHUB_DOMAIN + ROOT_REPO
                    }
                    dir('app-repo/profile-box-comm') {
                        git branch: 'main', url: 'http://' + GITHUB_DOMAIN + COMM_REPO
                    }
                    dir('app-repo/profile-box-api-user') {
                        git branch: 'main', url: 'http://' + GITHUB_DOMAIN + API_USER_REPO
                    }
//                             GitHub 레포지토리 체크아웃
//                             git branch: 'main',
//                                 credentialsId: 'your-credentials-id',
//                                 url: 'https://github.com/your-username/your-repo.git'
//
//                             // 특정 서브프로젝트 디렉토리로 이동
//                             dir('path/to/subproject') {
//                                 // 서브프로젝트 체크아웃
//                                 git branch: 'master',
//                                     credentialsId: 'your-credentials-id',
//                                     url: 'https://github.com/your-username/your-repo.git'
//                              }
             }
        }

        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                dir('app-repo') {
                    withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH=${tool 'jdk17'}/bin:${env.PATH}", "GRADLE_HOME=${tool 'gradle'}", "PATH=${tool 'gradle'}/bin:${env.PATH}"]) {
                        sh 'gradle clean build'
                    }
                }
//                 dir('app-repo') {
//                     git branch: 'main', url: 'http://' + GITHUB_DOMAIN + ROOT_REPO
//
//                     sh 'ant clean'
//                     sh 'ant package'
//                     sh 'ant deploy'
//                 }
            }
        }

        stage('Upload to Docker Registry') {
            steps {
                // 도커 이미지 빌드 및 업로드
                dir('app-repo/profile-box-api-user') {
                    withCredentials([usernamePassword(credentialsId: 'docker_login_info', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG} ."
                        sh 'docker login -u ${USERNAME} -p ${PASSWORD}'
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }

//         stage('Update ArgoCD YAML') {
//             steps {
//                 // ArgoCD YAML 파일 체크아웃
//                 dir('yarml-repo') {
//
//                     git branch: 'main', url: 'http://' + GITHUB_DOMAIN + YAML_REPO
//
//                     script {
//                         // YAML 파일에서 이미지 버전 변경
//                         sh 'sed -i "s|' + DOCKER_IMAGE +':.*|' + DOCKER_IMAGE + ':' + DOCKER_IMAGE_TAG +'|" deployment.yaml'
//                     }
//
//                     // 변경된 YAML 파일 커밋 및 푸시
//                     withCredentials([usernamePassword(credentialsId: 'git_email', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                         sh 'git config user.email ${USERNAME}'
//                     }
//                     withCredentials([usernamePassword(credentialsId: 'git_name', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                         sh 'git config user.name ${USERNAME}'
//                     }
//                     sh 'git add deployment.yaml'
//                     sh 'git commit -m "Update image version"'
//
//                     withCredentials([usernamePassword(credentialsId: 'gitea_token', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                         sh 'git push http://${USERNAME}:${PASSWORD}@${GITHUB_DOMAIN}${YAML_REPO}'
//                     }
//                 }
//             }
//         }
    }

//     post {
//         failure {
//             slackSend channel: '#k8s-cicd',
//             color: 'danger',
//             message: "${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG} Build failed!",
//             tokenCredentialId: 'slack_webhook'
//         }
//
//         success {
//             slackSend channel: '#k8s-cicd',
//             color: 'good',
//             message: "${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG} Build successful!",
//             tokenCredentialId: 'slack_webhook'
//         }
//     }
}