pipeline {
  agent any
  stages {
    stage('初始化变量') {
      steps {
        script {
          if (env.TAG_NAME ==~ /.*/ ) {
            env.ARTIFACT_VERSION = "${env.TAG_NAME}"
          } else if (env.MR_SOURCE_BRANCH ==~ /.*/ ) {
            env.ARTIFACT_VERSION = "mr-${env.MR_RESOURCE_ID}-${env.GIT_COMMIT_SHORT}"
          } else {
            // env.ARTIFACT_VERSION = "${env.BRANCH_NAME.replace('/', '-')}-${env.GIT_COMMIT_SHORT}"
            env.ARTIFACT_VERSION = "devel"
          }
        }
      }
    }
    stage('检出') {
      steps {
        checkout([$class: 'GitSCM',
        branches: [[name: GIT_BUILD_REF]],
        userRemoteConfigs: [[
          url: GIT_REPO_URL,
          credentialsId: CREDENTIALS_ID
        ]]])
      }
    }
    stage('登录docker仓库') {
      steps {
        sh '''# 隐藏密码
set +x
echo ${PROJECT_TOKEN} | docker login -u ${PROJECT_TOKEN_GK} --password-stdin ${DOCKER_REPO}
set -x
'''
      }
    }
    stage('构建镜像并推送到 CODING Docker 制品库') {
      steps {
        dir('./netty-gateway') {
          sh '''mvn clean package
docker build -t ${DOCKER_REPO}/${DOCKER_NAME}:${ARTIFACT_VERSION} .
docker push ${DOCKER_REPO}/${DOCKER_NAME}:${ARTIFACT_VERSION}
'''
        }

      }
    }
  }
  environment {
    DOCKER_REPO = 'arche-docker.pkg.coding.net/arche.cloud/arche-cloud'
    DOCKER_NAME = 'cloudarg'
    DOCKER_REPO_NAME = 'arche-cloud'
  }
}