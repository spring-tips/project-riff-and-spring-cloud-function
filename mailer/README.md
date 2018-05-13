# Mailer using Sendgrid in Kubernetes 

This function requires an environment variable `SENDGRID-API-KEY`. In order to make this work i put the secret in a file:

```
echo $SENDGRID_API_KEY > api-key.txt 
```

then I created a Kubernetes secret using that value: 

```
kubectl create secret generic sendgrid-api-key --from-file=sendgrid-api-key=./api-key.txt
```

Note that the value for `--from-file` specifies both a key name (`sendgrid-api-key`) and a key value (`./api-key.txt`). 

Then I built the Java artifact (`mvn clean package`) and created the function and deployed it:

```
 riff create java -i mailer -n mailer -a target/mailer-0.0.1-SNAPSHOT.jar --handler "mail&main=com.example.mailer.MailerApplication"  --force
```

Then, finally, I updated the resulting `-function.yml` file created when deploying the function. This time, I updated the configuration to reference the new secrets, adding an `env` stanza, like this: 

```
---
apiVersion: projectriff.io/v1alpha1
kind: Function
metadata:
  name: mailer
spec:
  container:
    image: jlong/mailer:0.0.1
    env:
      - name: SENDGRID_API_KEY
        valueFrom:
          secretKeyRef:
            name : sendgrid-api-key
            key: sendgrid-api-key
  input: mailer
  protocol: grpc
```

Then, finally, to get the hwole thing working again, I ran: 

```
riff update
```




