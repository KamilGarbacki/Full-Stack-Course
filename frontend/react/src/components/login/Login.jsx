import {
    Button,
    Flex,
    Text,
    Heading,
    Stack,
    Image, Link,
} from '@chakra-ui/react'
import {Formik, Form} from "formik";
import * as Yup from 'yup'
import TextInput from "../shared/TextInput.jsx";
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const LoginForm = () => {

    const { login } = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup
                        .string()
                        .email("Email must be valid")
                        .required("Email is required"),
                    password: Yup
                        .string()
                        .min(8, 'Must be between 8 to 20 characters')
                        .max(20, 'Must be between 8 to 20 characters')
                        .required("Password is required")
                })
            }
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting})=>{
                setSubmitting(true);
                login(values).then(() => {
                    navigate("/dashboard")
                }).catch(err => {
                    errorNotification(
                        err.code,
                        err.response.data.message
                    );
                }).finally(()=>{
                    setSubmitting(false);
                })
            }}
        >
            {({isValid, isSubmitting})=>(
                <Form>
                    <Stack spacing={14}>
                        <TextInput
                            label={"Email"}
                            name={"username"}
                            type={"email"}
                            placeholder={"email"}
                        />

                        <TextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                        />

                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}
                        >
                            Login
                        </Button>
                    </Stack>
                </Form>
            )}
        </Formik>
    )
}

const Login = () => {

    const { customer } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={'https://user-images.githubusercontent.com/40702606/210880158-e7d698c2-b19a-4057-b415-09f48a746753.png'}
                        boxSize={"200px"}
                        alt={"Logo"}
                        ml={'50px'}
                    />
                    <Heading fontSize={'2xl'} mb={6}>Sign in to your account</Heading>
                    <LoginForm />
                    <Link onClick={()=>{ navigate("/register") }}>
                        Don't have an account? Register here!
                    </Link>
                </Stack>
            </Flex>
            <Flex flex={1}
                  p={10}
                  flexDirection={"column"}
                  alignItems={"center"}
                  justifyContent={"center"}
                  bgGradient={{sm: 'linear(to-tr, teal.500, green.500)'}}
            >
                <Text fontSize={"6xl"} color={'white'} fontWeight={"bold"} mb={5}>
                    <Link href={"https://amigoscode.com/courses"}>
                    Enrol Now
                </Link></Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;