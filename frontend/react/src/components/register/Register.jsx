import {
    Flex,
    Box,
    Stack,
    Heading,
    useColorModeValue,
} from '@chakra-ui/react'
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";
import {successNotification} from "../../services/notification.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";

export default function RegisterCard() {
    const  {login}  = useAuth();
    const navigate = useNavigate()

    return (
        <Flex
            minH={'100vh'}
            align={'center'}
            justify={'center'}
            bgGradient={{sm: 'linear(to-tr, teal.500, green.500)'}}>
            <Stack spacing={5} mx={'auto'} maxW={'lg'} py={6} px={6}>
                <Stack align={'center'}>
                    <Heading color={'white'} fontSize={'4xl'} textAlign={'center'}>
                        Sign up
                    </Heading>
                </Stack>
                <Box
                    rounded={'lg'}
                    bg={useColorModeValue('white', 'gray.700')}
                    boxShadow={'lg'}
                    p={5}
                    minWidth={400}
                >
                        <CreateCustomerForm
                            onSuccess={(customer)=>{
                                successNotification(
                                    "Registered Successfully",
                                    `Your account was successfully saved`
                                )
                                login({username: customer.email, password: customer.password});
                                navigate("/dashboard");
                            }}
                        />

                </Box>
            </Stack>
        </Flex>
    )
}