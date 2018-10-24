package com.jk51.modules.account.job;

import com.beust.jcommander.internal.Lists;
import com.jk51.commons.dto.ReturnDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ParallelProcessUtils<T> extends RecursiveTask {

    private List<T> processList;
    Function<T,ReturnDto> function;
    List<ReturnDto> list;

    public ParallelProcessUtils(List<T> processList,Function<T,ReturnDto> function){
        this.processList=processList;
        this.function=function;
        this.list=new ArrayList<>();
    }

    @Override
    protected List<ReturnDto> compute() {
        if(processList.size()>1){
            processList.forEach(g->{
                ParallelProcessUtils<T> processUtils=new ParallelProcessUtils(Lists.newArrayList(g),function);
                ForkJoinTask<List<ReturnDto>> fk=processUtils.fork();
                try {
                    list.addAll(fk.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }else{
            list.add(function.apply(processList.get(0)));
        }
        return list;
    }
}
