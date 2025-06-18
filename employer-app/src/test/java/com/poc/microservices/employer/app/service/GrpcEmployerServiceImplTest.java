package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.dto.EmployeeJobDto;
import com.poc.microservices.employer.app.proto.GrpcEmployerServiceImpl;
import com.poc.microservices.proto.EmployeeListRequest;
import com.poc.microservices.proto.EmployerJobInfoList;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class GrpcEmployerServiceImplTest {

    @Mock
    private EmployeeJobService employeeJobService;

    private GrpcEmployerServiceImpl grpcEmployerService;

    @SuppressWarnings("unchecked")
    private final StreamObserver<EmployerJobInfoList> responseObserver = Mockito.mock(StreamObserver.class);

    @BeforeEach
    void setUp() {
        grpcEmployerService = new GrpcEmployerServiceImpl(employeeJobService);
    }

    @Test
    void testGetEmployerJobInfo() {
        List<EmployeeJobDto> mockDtos = List.of(
                new EmployeeJobDto(1001L, 5001L, List.of(2001L, 2002L))
        );

        Mockito.when(employeeJobService.getEmployeeJobInfo(Mockito.anyList())).thenReturn(mockDtos);

        EmployeeListRequest request =
                EmployeeListRequest.newBuilder().addAllEmployeeIds(Stream.of(1001L).map(Math::toIntExact).toList()).build();
        grpcEmployerService.getEmployerJobInfo(request, responseObserver);

        ArgumentCaptor<EmployerJobInfoList> responseCaptor = ArgumentCaptor.forClass(EmployerJobInfoList.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        EmployerJobInfoList response = responseCaptor.getValue();
        Assertions.assertEquals(1, response.getJobInfosCount());
        Assertions.assertEquals(5001L, response.getJobInfos(0).getEmployerId());
        Assertions.assertEquals(Stream.of(2001L, 2002L).map(Math::toIntExact).toList(), response.getJobInfos(0).getJobIdsList());
    }
}