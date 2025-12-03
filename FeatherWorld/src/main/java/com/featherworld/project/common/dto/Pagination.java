package com.featherworld.project.common.dto;

public class Pagination {

	// 절대적인 값
	private int currentPage;	// 현재 페이지 번호
	private int listCount;		// 전체 객체 수
	
	/* 상대적인 값을 정하는 기준 */
	private int limit;			// 한 페이지 목록에 보여지는 객체 수
	private int pageSize;		// 보여질 페이지 번호 개수
	
	// -------------------------------------------------------------
	
	// 상대적인 값
	private int maxPage;	// 마지막 페이지 번호
	private int startPage;	// 보여지는 맨 앞 페이지 번호
	private int endPage;	// 보여지는 맨 뒤 페이지 번호
		
	private int prevPage;	// 이전 페이지 모음의 마지막 번호
	private int nextPage;	// 다음 페이지 모음의 시작 번호
	
	public Pagination(int currentPage, int listCount) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		// limit, pageSize 지정해주지 않았을 때의 기본값 설정
		limit = 3;
		pageSize = 8;
		
		calculate();
	}

	public Pagination(int currentPage, int listCount, int limit, int pageSize) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		this.limit = limit;
		this.pageSize = pageSize;
		
		calculate();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getListCount() {
		return listCount;
	}

	public int getLimit() {
		return limit;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		
		calculate();
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
		
		calculate();
	}

	public void setLimit(int limit) {
		this.limit = limit;
		
		calculate();
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		
		calculate();
	}
	
	/** 상대적인 필드 값들을 계산해서 대입
	 * @author Jiho
	 */
	public void calculate() {
		
		// 전체 객체 수에서 한 페이지당 보여지는 개수를 나눈 값 올림
		maxPage = (int)Math.ceil((double)listCount / limit);
		
		startPage = (currentPage - 1) / pageSize * pageSize + 1;
		
		endPage = pageSize - 1 + startPage;
		
		// 페이지 끝 번호가 최대 페이지 수 초과
		if(endPage > maxPage) endPage = maxPage;
		
		// 이전으로 갈 페이지 없음
		if(currentPage <= pageSize) {
			prevPage = 1;
			
		} else {
			prevPage = startPage - 1;
		}
		
		// 다음으로 갈 페이지 없음
		if(endPage == maxPage) {
			nextPage = maxPage;
			
		} else {
			nextPage = endPage + 1;
		}
	}
	
	
}
