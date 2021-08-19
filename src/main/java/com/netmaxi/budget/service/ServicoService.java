package com.netmaxi.budget.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.netmaxi.budget.model.Servico;
import com.netmaxi.budget.repository.ServicoRepository;

@Service
public class ServicoService {
	@Autowired
	public ServicoRepository servicoRepository;

	public Page<Servico> listar(String search, Boolean ativo, Pageable pagination) {
		if (search == null)
			return ativo == null ? servicoRepository.findAll(pagination)
					: ativo ? servicoRepository.findByAtivoTrue(pagination)
							: servicoRepository.findByAtivoFalse(pagination);

		return ativo == null ? servicoRepository.findByNomeContaining(search, pagination)
				: ativo ? servicoRepository.findByNomeContainingAndAtivoTrue(search, pagination)
						: servicoRepository.findByNomeContainingAndAtivoFalse(search, pagination);
	}

	public Optional<Servico> getServicoPorId(Long id) {
		return servicoRepository.findById(id);
	}

	public Servico criar(Servico servico) {
		servico.setAtivo(true);
		return servicoRepository.save(servico);
	}

	public Servico atualizar(Long id, Servico servico) {
		Optional<Servico> servicoBuscado = getServicoPorId(id);
		Servico servicoEncontrado = null;
		if (servicoBuscado.isPresent()) {
			servicoEncontrado = servicoBuscado.get();
			BeanUtils.copyProperties(servico, servicoEncontrado, "id");
			return servicoRepository.save(servicoEncontrado);
		}
		return servicoEncontrado;
	}

	public ResponseEntity<?> delete(Long id) {
		Optional<Servico> servicoBuscado = getServicoPorId(id);
		if(servicoBuscado.isPresent()) {
			Servico servicoEncontrado = servicoBuscado.get();
			servicoEncontrado.setAtivo(false);
			servicoRepository.save(servicoEncontrado);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado"); 
	}

}
